package com.mapaaa.mapacertooo;

public class QuestionModel {
    public QuestionModel(String questionString, String answer){
        QuestionString = questionString;
        Answer = answer;

    }
    public String getQuestionString (){
        return QuestionString;
    }
    public  void setQuestioString(String questioString){
        QuestionString =questioString;
    }
    public String getAnswer() {
        return Answer;
    }
    public void setAnswer(String answer){
        Answer = answer;
    }
    private String QuestionString;
    private String Answer;
}

