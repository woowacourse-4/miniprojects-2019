package com.woowacourse.zzinbros.user.domain;

import com.woowacourse.zzinbros.user.exception.IllegalUserArgumentException;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
public class TempUser implements Serializable {

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 10;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 30;
    private static final String EMAIL_PATTERN = "^.+@.+$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = MAX_PASSWORD_LENGTH)
    private String password;

//    @OneToMany(mappedBy = "from")
//    @NotFound(action = NotFoundAction.IGNORE)
//    private Set<Friend> friends = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdTime;

    @UpdateTimestamp
    private LocalDateTime lastModifiedTime;

    public TempUser() {

    }

    public TempUser(String name, @Email String email, String password) {
        validateLength(name, MIN_NAME_LENGTH, MAX_NAME_LENGTH);
        validateLength(password, MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH);
        validatePattern(email, EMAIL_PATTERN);

        this.name = name;
        this.email = email;
        this.password = password;
    }

    private void validateLength(String name, int minNameLength, int maxNameLength) {
        if (!matchLength(name, minNameLength, maxNameLength)) {
            String message = String.format("길이가 %d 이상 %d 미만이어야 합니다", MIN_NAME_LENGTH, MAX_NAME_LENGTH);
            throw new IllegalUserArgumentException(message);
        }
    }

    private void validatePattern(String element, String pattern) {
        if (!matchRegex(element, pattern)) {
            throw new IllegalUserArgumentException();
        }
    }

    private boolean matchLength(String input, int min, int max) {
        return (input.length() >= min && input.length() < max);
    }

    private boolean matchRegex(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    public void update(TempUser updatedUser) {
        this.name = updatedUser.name;
        this.email = updatedUser.email;
        this.password = updatedUser.password;
    }

//    public boolean isFollowTo(TempUser to) {
//        return friends.stream()
//                    .anyMatch(friend -> friend.isFollowTo(to));
//    }
//
//    public boolean addFriend(Friend friend) {
//        return friends.add(friend);
//    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

//    public Set<Friend> getFriends() {
//        return friends;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        TempUser tempUser = (TempUser) o;
        return Objects.equals(this.id, tempUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
