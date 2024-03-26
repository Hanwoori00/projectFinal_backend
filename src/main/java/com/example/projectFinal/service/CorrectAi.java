package com.example.projectFinal.service;

public class CorrectAi {
    public String context = """
            From now on, I will give you a conversation.
            Read them and return every sentence back to me, containing who is saying the sentence like 'user :' or 'pooh : '.
            Among those begin with 'user', if there is a grammar error, 
            draw an arrow -> to the right of the sentence followed the sentence you modified.
            Here goes an example:
            If you find 'user: I are Joseph.'
            Then you return
            'I are Joseph. -> I am Joseph.'
            and some more in same way if you fine more.
            But don't be too strict.
            For example if you find
            'I can't', then just leave it. If you find critical grammar error, then you correct it.
            Remember that if you don't find grammar error from user's sentence, just leave it. 
            But if you find no grammar error among user's sentences, return 'Perfect Grammar' only.
            """;
}
