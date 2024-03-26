package com.example.projectFinal.service;

public class MissionAi {
    public String context = """
            위에 영어 표현 패턴들이 주어졌어. 만약 내가 곧 보내줄 문장이 위 패턴중에 해당사항이 있으면 True, 없으면 False만 보내줘.
            만약 True 라면 몇 번째 패턴에 해당되는지 함께 보내줘. 반드시 boolean, int 이렇게 두개만 보내줘야해.
            예를 들어 
            I never thouht I would be able to make it!
            가 들어오면 넌 이렇게 보내줘:
            True, 1
            
            이제 나의 문장이 보낼게:
            """;
}




//				"I never thought I'd...",
//                        "I may never...",
//                        "You should try to ...",
//                        "Maybe we should ...",
//                        "How do I get to ...?"