package com.example.adeen_s.quiz;

/**
 * Created by adeen-s on 10/8/17.
 * This class is used to generate objects that will have the question , the answer and the other incorrect answers
 */

public class Question {
    protected String question;
    protected String answer;
    protected String option1;
    protected String option2;
    protected String option3;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
