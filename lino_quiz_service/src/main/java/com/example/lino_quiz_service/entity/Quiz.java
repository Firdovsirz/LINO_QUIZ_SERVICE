package com.example.lino_quiz_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "quiz_serial_number", unique = true, nullable = false)
    private Integer quizSerialNumber;

    @Column(name = "title")
    private String title;

    @Column(name = "type")
    private int type; // 1 - admin, 2 - teacher

    @Column(name = "is_free")
    private int isFree; // 0 - paid, 1 - free

    public Quiz() {}

    public Quiz(Integer quizSerialNumber, String title, int type, int isFree) {
        this.quizSerialNumber = quizSerialNumber;
        this.title = title;
        this.type = type;
        this.isFree = isFree;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuizSerialNumber() {
        return quizSerialNumber;
    }

    public void setQuizSerialNumber(Integer quizSerialNumber) {
        this.quizSerialNumber = quizSerialNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIsFree() {
        return isFree;
    }

    public void setIsFree(int isFree) {
        this.isFree = isFree;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", quizSerialNumber=" + quizSerialNumber +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", isFree=" + isFree +
                '}';
    }
}
