package com.example.projectFinal.service;

import org.springframework.stereotype.Service;

@Service
public class CorrectAi {
    public String context = """
            From now on, I will give you a conversation.
            Read them and return every sentence back to me, containing who is saying the sentence like 'user :' or 'pooh : '.
            Among those begin with 'user', if there is a grammar error, 
            draw an arrow -> to the right of the sentence followed the sentence you modified.
            Here goes an example:
            INPUT
            user: Hi! 
            pooh: Oh, hello there! Is it you, my friend? What's your name? 
            user: I'm Joseph. What are your name?
            pooh: Oh, how lovely to meet you, Joseph! My name is Pooh, silly old bear. What a beautiful day, isn't it?
            OUTPUT
            user: Hi!
            pooh: Oh, hello there! Is it you, my friend? What's your name?
            user: I'm Joseph. What are your name? -> What is your name?
            pooh: Oh, how lovely to meet you, Joseph! My name is Pooh, silly old bear. What a beautiful day, isn't it?
            
            Make sure you must find errors among sentence beginning with 'user' only. Do not ever ones that don't begin with 'user'.
            But here's some exceptions(don't be too strict).
            'I can't', 'Good' can be considered uncompleted sentence, but as far as it can be considered to be skipped, just leave it.
            And don't mind marks. Just focus on letters. Don't need to change 'Good!' into 'Good.'
            If you find critical grammar error, then you correct it.
            Remember that if you don't find grammar error from user's sentence, just leave it. 
            But if you find no grammar error among user's sentences, return 'Perfect Grammar' only.
            """;
}
