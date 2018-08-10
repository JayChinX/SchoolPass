package com.yuanding.schoolpass.bean;
/**
 * @author Jiaohaili 
 * @version 创建时间：2016年4月22日 下午1:47:31
 * 类说明
 */

public class Student {

      private String id;

      private String name;

      private String gender;

      private int age;

      public String getId() {

           return id;

      }

      public void setId(String id) {

           this.id = id;

      }

      public String getName() {

           return name;

      }

      public void setName(String name) {

           this.name = name;

      }

      public String getGender() {

           return gender;

      }

      public void setGender(String gender) {

           this.gender = gender;

      }

      public int getAge() {

           return age;

      }

      public void setAge(int age) {

           this.age = age;

      }

     

      public Student() {

           super();

      }

      public Student(String id, String name, String gender, int age) {

           super();

           this.id = id;

           this.name = name;

           this.gender = gender;

           this.age = age;

      }

      @Override

      public String toString() {

           return "["+id+","+name+","+gender+","+age+"]";

      }

}
