package com.example.projectFinal.service;

public class PracticeAi {
    public String context = """
너에게 영어 표현을 줄거야. 
이 영어표현을 통해 영어 회화 공부를 하는데 도움이 되는 문장과 대화를 나에게 넘겨줘.
먼저 그 표현을 사용한 문장 세개를 줘. 
이 문장들은 주로 취미나 학교, 가족, 친구 등의 특수한 상황을 랜덤하게 반영해줘.
그리고 나서 그 두 문장으로 구성된 대화를 하나 줘. 
이때 그 표현은 두 문장중 한 문장에만 포함시켜.
첫번째 문장에 포함시켜도 되고 두번째 문장에 포함시켜도 돼.
이 대화는 주로 학교나 친구, 회사, 모임, 클럽, 취미 등의 특수한 상황을 반영해줘.
그리고 이 모든걸 쉼표로 나누어 다섯개의 parameter로 읽힐수 있도록 보내줘. 
반드시 너는 나에게 총 다섯개의 문장만 보내줘야해. 
그리고 각 다섯문장 뒤에는 괄호가 열리고 그 안에 한국어로 해석된 문장이 뒤따라서 짝을 이루어야해. 
반드시 다섯문장은 ',, '쉼표두개로 연결되어 있어야해.
그러니까 너가 반환해야 하는건 이거야 : 첫번째 문장(한국어 번역),, 두번째 문장(한국어 번역),, 세번째 문장(한국어 번역),, 대화의 첫마디(한국어 번역),, 대화의 두번째마디(한국어 번역) 
예시를 보여줄게.
'I am trying to...'이걸 만약 input으로 받는다면
[I am trying to do my best.(난 최선을 다하려고 노력하고 있어.),, She was trying to understand what you mean.(그녀는 내가 무슨말을 하는지 이해하려고 노력했어요.),, I was trying to finish it.(난 그걸 다 먹으려고 했지.),, A: Are you sure about this work?(너 이게 맞다고 생각하니?),, B: I was trying to do my best, but it didn't go well.(나도 최선을 다하려고 했는데 잘 안됐어.).]
이렇게 답해주면 돼.

Here goes the input:""";
}



