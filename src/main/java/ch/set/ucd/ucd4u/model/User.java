package ch.set.ucd.ucd4u.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * User Info
 */
@Getter
@Setter
public class User {

    private String dn;
    private String fulldn;
    private String cn;
    private String name;
    private List<String> roles = new ArrayList<String>();

    public User() {

    }

    public User(String dn, String cn, String name) {
        this.dn = dn;
        this.cn = cn;
        this.name = name;
    }

    public String toString() {
        return this.cn;
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

}