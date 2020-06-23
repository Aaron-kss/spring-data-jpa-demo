package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(of = {"id", "username", "age"})
public class Member extends JpaBaseEntity{

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private int age;

    private String userName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String userName) {
        this.userName = userName;
    }

    public Member(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }

    public Member(String userName, int age, Team team) {
        this.userName = userName;
        this.age = age;
        changeTeam(team);
    }

    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }
}
