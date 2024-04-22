# Project Dorun-Dorun
개발기간 : 2024년 2월28일 ~ 4월 1일(35일)




## 배포 주소
https://43.203.227.36.sslip.io/

(서버 정비중 ~4/23)





## 주요 기능



- ### AI와 대화하기
  
![Last](https://github.com/Hanwoori00/projectFinal_backend/assets/148731022/9007e4cf-82bc-4487-b3c4-95c6de32fe95)

(마이크를 사용하지 않은 예. 감정에 따라 캐릭터의 표정이 바뀐다. 응답레이턴시는 총 3초.)




- ### 새로운 표현 배우기
  
![222](https://github.com/Hanwoori00/projectFinal_backend/assets/148731022/91079bae-e242-422b-a593-38122ed5b45d)

(클라이언트의 수준에 맞는 문장표현과 연습할수 있는 다수의 랜덤한 예문을 AI가 만들어 준다.)



- ### 미션관리
  
<img width="888" alt="image" src="https://github.com/Hanwoori00/projectFinal_backend/assets/148731022/3dc5f837-7847-416e-b040-d5b2d7a95004">

(클라이언트가 캐릭터와 대화하는 도중 앞서 학습한 표현을 활용할 수 있도록 미션을 부여한다. 미션검사AI를 모델링하여 해당 표현을 활용했는지 검사하고, 성공시 빵빠레.)




- ### 문법 교정

<img width="905" alt="image" src="https://github.com/Hanwoori00/projectFinal_backend/assets/148731022/32124644-a731-4ad5-9585-a9ce7997e6ea">

(대화가 종료되면 문법 교정AI 모델이 대화 내역을 검사한다. 틀린 내역이 있으면 수정하여 준다.)




## 스택 및 서버 구축 개요

<img width="1016" alt="image" src="https://github.com/Hanwoori00/projectFinal_backend/assets/148731022/d274baa7-965e-4d85-b5c9-89398578b75f">
<img width="1226" alt="image" src="https://github.com/Hanwoori00/projectFinal_backend/assets/148731022/c8469719-7a61-4355-b4e9-3ac5533c9e4f">

클라이언트와 대화형 AI 모델 캐릭터의 원활한 대화를 위해
1. 캐릭터의 목소리(Eleven Labs API)
2. 캐릭터의 성격과 배경지식, 그리고 말투(PaLM2 API)
3. 빠르고 정확한 클라이언트 음성 수집을 통한 매끄러운 대화전개(Google Cloud Speech API)
세가지 API 채택.

+) 클라이언트의 음성 녹음본을 입력 받기 위해 https의 ssl 필요. 이에 'caddy'로 서버 요청 분배.





## 개발팀 소개

|이름|담당|역할|
|----|--|------|
|한우리|백엔드|AI 모델링(Chat-bison 챗 모델, 문법 검사 Text 모델, 문장 학습 예문제공 Text 모델), Mission 기능, Practice 기능 구현 및 DB 설계|
|이정원|백엔드|서버구축, 보안, TTS API, User 관리|
|심세연|풀스택|STT API, ChatModel 채택, AI 모델링(미션 성공 여부 확인 Text 모델), Mission 기능 구현 및 DB 설계|
|김상호|프론트|대화하기 목록, 메인 채팅 페이지|
|손호성|프론트|유저/로그페이지, 퀴즈|
|한지혜|프론트|CSS 전반, 메인페이지, 마이페이지, 학습페이지|

