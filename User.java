package com.info.except;
import ch.qos.logback.core.subst.Tokenizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Controller
@RestController
public class User {
    private List<String> users = new ArrayList<>();
    private List<String> passwords = new ArrayList<>();
    private List<String> ages = new ArrayList<>();


    private Map<String ,String> ua = new HashMap<String ,String>();
    private ArrayList<String> values = new ArrayList<>();
    private ArrayList<String> keys = new ArrayList<>();



    @GetMapping("getSingleUser/{username}")
    public String getUser(@PathVariable("username") String s){
        boolean check = false;
        String agge="0";
        for (int i = 0, usersSize = users.size(); i < usersSize; i++) {
            String user = users.get(i);
            if (user.equals(s)) {
                check = true;
                agge = ages.get(i);
                break;
            }
        }
        if(!check){
            throw new NotFoundException();
        } else{
            return s + " " + agge;
        }

    }
    @GetMapping("getListOfUsers/{age}/{direction}")
    public Map<String, String> addUser(@PathVariable("age") Integer age,@PathVariable("direction") String dir){
        ua.clear();
        values.clear();
        keys.clear();
        for(int i=0;i< ages.size();i++){
            if((Integer.parseInt(ages.get(i))>=age-5)&&(Integer.parseInt(ages.get(i))<=age+5)){
                //ua.put(users.get(i), ages.get(i));
                keys.add(users.get(i));
                values.add(ages.get(i));

            }
        }

        String  v;
        String  k;
        v="0";
        k="0";

        if(dir.equals("up")) {
            for (int a1 = 0; a1 < keys.size(); a1++) {
                for (int a2 = a1; a2 < keys.size(); a2++) {
                    if (Integer.parseInt(values.get(a1)) > Integer.parseInt(values.get(a2))) {
                        v = values.get(a1);
                        k = keys.get(a1);
                        values.add(a1, values.get(a2));
                        values.remove(a1+1);
                        keys.add(a1,keys.get(a2));
                        keys.remove(a1+1);
                        values.add(a2, v);
                        values.remove(a2+1);
                        keys.add(a2,k);
                        keys.remove(a2+1);

                    }
                }
            }
        }
        if(dir.equals("down")){
            for (int a1 = 0; a1 < keys.size(); a1++) {
                for (int a2 = a1; a2 < keys.size(); a2++) {
                    if (Integer.parseInt(values.get(a1)) < Integer.parseInt(values.get(a2))) {
                        v = values.get(a1);
                        k = keys.get(a1);
                        values.add(a1, values.get(a2));
                        values.remove(a1+1);
                        keys.add(a1,keys.get(a2));
                        keys.remove(a1+1);
                        values.add(a2, v);
                        values.remove(a2+1);
                        keys.add(a2,k);
                        keys.remove(a2+1);
                    }
                }
            }
        }
        //ua.clear();
        for(int i=0;i< keys.size();i++){
            ua.put(keys.get(i), values.get(i));
        }
        return ua;
    }

    @PostMapping("{User}/{Password}/{RepeatPassword}/{Age}")
    public void addUser(@PathVariable("User") String request1,@PathVariable("Password") String request2, @PathVariable("Age") String request3,@PathVariable("RepeatPassword") String request4){
        users.add(request1);
        passwords.add(request2);
        ages.add(request3);
        for(int i=0;i< users.size();i++) {
            if ((users.get(i).equals(users.get(users.size() - 1))) && (users.size() - 1 != i)) {
                users.remove(users.lastIndexOf(request1));
                passwords.remove(passwords.lastIndexOf(request2));
                ages.remove(ages.lastIndexOf(request3));
                throw new ConflictException();
            }


            }
        for(int i =0;i<request1.length();i++) {
            if ((!(((request1.charAt(i) >= 'a') && (request1.charAt(i) <= 'z')) || ((request1.charAt(i) >= 'A') && (request1.charAt(i) <= 'Z')) || ((request1.charAt(i) >= '0') && (request1.charAt(i) <= '9'))))||(!request2.equals(request4))) {
                users.remove(users.lastIndexOf(request1));
                passwords.remove(passwords.lastIndexOf(request2));
                ages.remove(ages.lastIndexOf(request3));
                throw new BadRequestException();
            }
        }




    }

    @DeleteMapping("delete/{deleted}")
    public void deleteUser(@PathVariable("deleted") String s1){
        boolean check = false;
        for (String user : users) {
            if (user.equals(s1.substring(5))) {
                check = true;
                break;
            }
        }
        if((s1.substring(0,5).equals("admin"))&&(s1.length()>5)){
            if (!check) {
                throw new NotFoundException();
            } else{
                for(int i=0;i< users.size();i++){
                    if(s1.substring(5).equals(users.get(i))){
                        users.remove(i);
                        passwords.remove(i);
                        ages.remove(i);
                    }
                }
            }
        } else {
            throw new ForbiddenException();

        }
    }
    @PutMapping("{username}/{newPassword}")
    public void refreshPassword(@PathVariable("username") String user,@PathVariable("newPassword") String pass){
        boolean check=false;
        for (String s : users) {
            if (s.equals(user.substring(6))) {
                check = true;
                break;
            }

        }



            if (!check) {
                throw new NotFoundException();
            } else{
                if((user.substring(0,6).equals("update"))&&(user.length()>6)){
                for(int i=0;i< users.size();i++){
                    if(user.substring(6).equals(users.get(i))){
                        passwords.add(i,pass);
                        passwords.remove(i+1);


                    }
                }
                } else {
                    throw new ForbiddenException();

                }
            }

        for(int i =0;i<user.substring(6).length();i++) {
            if (!(((user.substring(6).charAt(i) >= 'a') && (user.substring(6).charAt(i) <= 'z')) || ((user.substring(6).charAt(i) >= 'A') && (user.substring(6).charAt(i) <= 'Z')) || ((user.substring(6).charAt(i) >= '0') && (user.substring(6).charAt(i) <= '9')))) {
                throw new BadRequestException();
            }
        }


    }
    public class NotFoundException extends ResponseStatusException {
        public NotFoundException() {
            super(HttpStatus.NOT_FOUND, "not found");
        }
    }
    public class ForbiddenException extends ResponseStatusException {
        public ForbiddenException() {
            super(HttpStatus.FORBIDDEN, "error");
        }
    }
    public class ConflictException extends ResponseStatusException {
        public ConflictException() {
            super(HttpStatus.CONFLICT, "conflict");
        }
    }
    public class BadRequestException extends ResponseStatusException {
        public BadRequestException() {
            super(HttpStatus.BAD_REQUEST, "bad request");
        }
    }
}
