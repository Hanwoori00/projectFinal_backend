package com.example.projectFinal.repository;

import com.example.projectFinal.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, String> {

    // course 에 해당하는 mission 찾기
    List<MissionEntity> findByCourse(String course);

    MissionEntity findByMissionId(String missionId);

    List<MissionEntity> findByMissionIdIn(List<String> missionIds);
}
