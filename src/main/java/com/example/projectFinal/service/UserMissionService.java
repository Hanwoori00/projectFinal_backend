package com.example.projectFinal.service;

import com.example.projectFinal.dto.UserDto;
import com.example.projectFinal.dto.UserMissionDto;
import com.example.projectFinal.entity.MissionEntity;
import com.example.projectFinal.entity.User;
import com.example.projectFinal.entity.UserMissionEntity;
import com.example.projectFinal.repository.MissionRepository;
import com.example.projectFinal.repository.UserMissionRepository;
import com.example.projectFinal.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.aiplatform.v1beta1.EndpointName;
import com.google.cloud.aiplatform.v1beta1.PredictResponse;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceSettings;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class UserMissionService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MissionRepository missionRepository;

    @Autowired
    UserMissionRepository userMissionRepository;

    @Autowired
    UserService userService;

    // 학습하기 : course 선택 -> user_mission 테이블에 데이터 추가
    public void addUserMissionsForCourse(String course, String accessToken, String refreshToken) {
        UserDto.AuthuserDto authuserDto = userService.authuser(accessToken, refreshToken);

        if (!authuserDto.isResult()) {
            return;
        }

        // user 찾기
        String userId = authuserDto.getUserId();
        User user = userRepository.findByUserId(userId);


        // 이미 존재하는 미션인지 확인
        List<UserMissionEntity> existingMissions = userMissionRepository.findByUserIdAndMissionId_Course(user, course);
        if (!existingMissions.isEmpty()) {
            return; // 이미 해당 코스에 대한 미션 데이터가 있으면 더 이상 진행하지 않음
        }

        // 선택한 코스에 해당하는 미션 데이터
        List<MissionEntity> missions = missionRepository.findByCourse(course);

        for (MissionEntity mission : missions) {
            UserMissionEntity userMission = UserMissionEntity.builder()
                    .userId(user)
                    .missionId(mission)
                    .complete(false)
                    .learn(false)
                    .build();

            userMissionRepository.save(userMission);
        }

    }


    // 학습하기 : 프론트로 문장 전송
    public List<UserMissionDto> getUnLearnMissionsForUser(String course, String accessToken, String refreshToken) {
        UserDto.AuthuserDto authuserDto = userService.authuser(accessToken, refreshToken);
        if (!authuserDto.isResult()) {
            return Collections.emptyList();
        }
        // user 찾기
        String userId = authuserDto.getUserId();
        User user = userRepository.findByUserId(userId);

        // 학습 false 미션 가져오기
        List<UserMissionEntity> unLearnMissions =
                userMissionRepository.findByUserIdAndLearnAndMissionId_Course(user, false, course);

        if (unLearnMissions.isEmpty()) {
            return Collections.emptyList();
        }

        Collections.shuffle(unLearnMissions);
        List<UserMissionEntity> limitedUnlearnMissions = unLearnMissions.stream().limit(3).toList();

        List<UserMissionDto> result = new ArrayList<>();

        for (UserMissionEntity limitedUnlearnMission : limitedUnlearnMissions) {
            UserMissionDto userMissionDto = UserMissionDto.builder()
                    .missionId(limitedUnlearnMission.getMissionId().getMissionId())
                    .mission(limitedUnlearnMission.getMissionId().getMission())
                    .meaning(limitedUnlearnMission.getMissionId().getMeaning())
                    .complete(limitedUnlearnMission.isComplete())
                    .build();

            result.add(userMissionDto);
        }
        return result;

    }


    // 학습하기 : 학습 완료
    public void setLearnMissionsForUser(String accessToken, String refreshToken, String missionId) {
        UserDto.AuthuserDto authuserDto = userService.authuser(accessToken, refreshToken);
        if (!authuserDto.isResult()) {
            return;
        }

        // user 찾기
        String userId = authuserDto.getUserId();
        User user = userRepository.findByUserId(userId);

        // missionId 찾기
        MissionEntity mission = missionRepository.findByMissionId(missionId);

        // 해당 미션 데이터 찾기
        UserMissionEntity userMission =
                userMissionRepository.findByUserIdAndMissionIdAndLearn(user, mission, false);

        if (userMission == null) {
            System.out.println("학습 정보와 일치하는 유저 데이터가 없습니다.");
            return;
        }
        userMission.setLearn(true);
        userMissionRepository.save(userMission);

    }



    // 채팅창 : 프론트로 문장 전송
    public List<UserMissionDto> getUncompletedMissionsForUser(String accessToken, String refreshToken) {
        UserDto.AuthuserDto authuserDto = userService.authuser(accessToken, refreshToken);
        if (!authuserDto.isResult()) {
            return Collections.emptyList();
        }

        // user 찾기
        String userId = authuserDto.getUserId();
        User user = userRepository.findByUserId(userId);

        // 학습 true, 사용 false 미션 가져오기
        List<UserMissionEntity> unusedMissions =
                userMissionRepository.findByUserIdAndCompleteAndLearn(user, false, true);

        if (unusedMissions.isEmpty()) {
            return Collections.emptyList();
        }

        Collections.shuffle(unusedMissions);
        List<UserMissionEntity> limitedUnusedMissions = unusedMissions.stream().limit(3).toList();

        List<UserMissionDto> result = new ArrayList<>();

        for (UserMissionEntity limitedUnusedMission : limitedUnusedMissions) {
            UserMissionDto userMissionDto = UserMissionDto.builder()
                    .missionId(limitedUnusedMission.getMissionId().getMissionId())
                    .mission(limitedUnusedMission.getMissionId().getMission())
                    .meaning(limitedUnusedMission.getMissionId().getMeaning())
                    .complete(limitedUnusedMission.isComplete())
                    .build();

            result.add(userMissionDto);
        }
        return result;
    }


//    public List<UserMissionEntity> getUnusedMissionsForUser(String userId) {
//        // user 찾기
//        User user = userRepository.findByUserId(userId);
//        if (user == null) {
//            return Collections.emptyList();
//        }
//
//        // 사용하지 않은 미션 가져오기
//        List<UserMissionEntity> unusedMissions = userMissionRepository.findByUserIdAndUsed(user, false);
//
//        Collections.shuffle(unusedMissions);
//        return unusedMissions.stream().limit(3).collect(Collectors.toList());
//
//    }


    // 채팅창 : 미션 문장 사용여부 판단 AI
    public String textPrompt(String data) throws IOException {
        String prompt = makePrompt(data);
        String instance =
                "{ \"prompt\": " + "\"Check which expression from the missions the chat corresponds to and return the corresponding mission_id(s) as an Array. (e.g. ['lv1_1', 'lv_2']) "+
                        "If no matching missions are found or if the chat sentence does not match the expression from any of the missions, return none in lower case." +
                        prompt + "\"}";
        String parameters =
                "{\n"
                        + "  \"temperature\": 0.2,\n"
                        + "  \"maxOutputTokens\": 256,\n"
                        + "  \"topP\": 0.95,\n"
                        + "  \"topK\": 1\n"
                        + "}";
        String project = "teampj-final";
        String location = "asia-northeast3";
        String publisher = "google";
        String model = "text-bison@002";

        // 인증 파일의 경로를 설정합니다.
        String credentialsPath = "/home/ubuntu/.config/gcloud/application_default_credentials.json";

        return predictTextPrompt(instance, parameters, project, location, publisher, model, credentialsPath);
    }

    public String makePrompt(String data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(data);

            StringBuilder sb = new StringBuilder();
            for (JsonNode missionNode : jsonNode.get("missions")) {
                String missionId = missionNode.get("mission_id").asText();
                String mission = missionNode.get("mission").asText();
                sb.append("mission_id: ").append(missionId).append("\n")
                        .append("mission: ").append(mission).append("\n\n");
            }

            String chat = jsonNode.get("chat").asText();

            sb.append("chat: ").append(chat);
//            System.out.println(sb);

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    public String predictTextPrompt(
            String instance,
            String parameters,
            String project,
            String location,
            String publisher,
            String model,
            String credentialsPath
    ) throws IOException {
        // PredictionServiceClient를 생성하기 위한 설정을 구성합니다.
        PredictionServiceSettings predictionServiceSettings =
                PredictionServiceSettings.newBuilder()
                        .setEndpoint(location + "-aiplatform.googleapis.com:443")
                        .setCredentialsProvider(FixedCredentialsProvider.create(
                                GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
                        ))
                        .build();

        try (PredictionServiceClient predictionServiceClient =
                     PredictionServiceClient.create(predictionServiceSettings)) {
            final EndpointName endpointName =
                    EndpointName.ofProjectLocationPublisherModelName(project, location, publisher, model);

            com.google.protobuf.Value.Builder instanceValue = com.google.protobuf.Value.newBuilder();
            JsonFormat.parser().merge(instance, instanceValue);
            List<com.google.protobuf.Value> instances = new ArrayList<>();
            instances.add(instanceValue.build());

            Value.Builder parameterValueBuilder = com.google.protobuf.Value.newBuilder();
            JsonFormat.parser().merge(parameters, parameterValueBuilder);
            Value parameterValue = parameterValueBuilder.build();

            PredictResponse predictResponse =
                    predictionServiceClient.predict(endpointName, instances, parameterValue);

            for (Value prediction : predictResponse.getPredictionsList()) {
                if (prediction.getKindCase() == Value.KindCase.STRUCT_VALUE) {
                    Map<String, Value> predictionMap = prediction.getStructValue().getFieldsMap();

                    if (predictionMap.containsKey("content")) {
                        return predictionMap.get("content").getStringValue();
                    }
                }
            }

            return null;
        }
    }


    // 채팅창 : 미션 완료(대화 종료)
    public void SetMissionCompleteForUSer(String accessToken, String refreshToken, String[] missionIds) {
        UserDto.AuthuserDto authuserDto = userService.authuser(accessToken, refreshToken);
        if (!authuserDto.isResult()) {
            return;
        }

        // user 찾기
        String userId = authuserDto.getUserId();
        User user = userRepository.findByUserId(userId);

        // missionIds 찾기
        List<MissionEntity> missions = missionRepository.findByMissionIdIn(Arrays.asList(missionIds));

        // 해당 데이터 찾기
        List<UserMissionEntity> userMissions = userMissionRepository.findByUserIdAndMissionIdIn(user, missions);

        // 미션 완료 처리
        for (UserMissionEntity userMission : userMissions) {
            userMission.setComplete(true);
        }
        // 변경사항 DB에 반영
        userMissionRepository.saveAll(userMissions);
    }
}
