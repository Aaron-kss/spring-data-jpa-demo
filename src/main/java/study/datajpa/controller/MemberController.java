package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("members/{id}")
    public String fineMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUserName();
    }

    @GetMapping("members2/{id}")
    public String fineMember2(@PathVariable("id") Member member) {
        return member.getUserName();
    }


    @GetMapping("members")
    public Page<MemberDto> fineMember2(@PageableDefault(size = 5) Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);
    }
}
