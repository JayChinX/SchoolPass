
package com.yuanding.schoolpass.view.contact;

import com.yuanding.schoolpass.A_Main_My_Contact_Acy;
import com.yuanding.schoolpass.bean.Cpk_Persion_Contact;

import java.util.Comparator;

public class PinyinComparator implements Comparator<Cpk_Persion_Contact> {

    @Override
    public int compare(Cpk_Persion_Contact o1, Cpk_Persion_Contact o2) {
        if ("☆".equals(o1.getSortLetters())) {
            return -1;
        } else if ("☆".equals(o2.getSortLetters())) {
            return 1;
        } else if ("#".equals(o1.getSortLetters())) {
            return -1;
        } else if ("#".equals(o2.getSortLetters())) {
            return 1;
        } else {
            try {
                if (o1.getSortLetters() == null || o1.getSortLetters().equals("")) {
                    A_Main_My_Contact_Acy.logE("o1.getSortLetters() == null");
                    return -1;
                }
                if (o2.getSortLetters() == null || o2.getSortLetters().equals("")) {
                    A_Main_My_Contact_Acy.logE("o2.getSortLetters() == null");
                    return -1;
                }
                return o1.getSortLetters().compareTo(o2.getSortLetters());
            } catch (Exception e) {
                A_Main_My_Contact_Acy.logE("Exception e");
                return -1;
            }
        }
    }

}
