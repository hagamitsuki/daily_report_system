package models.validators;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import models.Employee;
import utils.DBUtil;

public class EmployeeValidator {
    public static List<String> validate(Employee e, Boolean code_duplicate_check_flag, Boolean password_check_flag){
        /*code_duplicate_check_flag：コード重複チェック*/
        List<String> errors = new ArrayList<String>();

        String code_error = _validateCode(e.getCode(), code_duplicate_check_flag);
        if(!code_error.equals("")){
            errors.add(code_error);/*"code_error"= 入力された～存在しています。（以下のメソッドの戻り値）*/
        }

        String name_error = _validateName(e.getName());
        if(!name_error.equals("")){
            errors.add(name_error);
        }

        String password_error = _validatePassword(e.getPassword(),password_check_flag);
        if(!password_error.equals("")){
            errors.add(password_error);
        }

        return errors;
    }


    private static String _validateCode(String code, Boolean code_duplicate_check_flag){

        if(code == null || code.equals("")){
            return "社員番号を入力してください。";
        }

        if(code_duplicate_check_flag){//Boolean型のcode_duplicate_check_flagがtrueだったら
            EntityManager em = DBUtil.createEntityManager();
            /*employees_countに、入力した社員番号がいくつあるかをカウントした結果を入れる（あれば1がかえる？）*/
            long employees_count = (long)em.createNamedQuery("checkRegisteredCode", Long.class)
                                            .setParameter("code", code)
                                            .getSingleResult();

            em.close();
            if(employees_count > 0){
                return "入力された社員番号の情報は既に存在しています。";
            }
        }

        return "";
    }

    private static String _validateName(String name) {
        if(name == null || name.equals("")) {
            return "氏名を入力してください。";
        }

        return "";
    }

    private static String _validatePassword(String password, Boolean password_check_flag) {
        if(password_check_flag && (password == null || password.equals(""))) {
            return "パスワードを入力してください。";
        }
        return "";
    }


}
