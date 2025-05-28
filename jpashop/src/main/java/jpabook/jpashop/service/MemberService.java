package jpabook.jpashop.service;

import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// 데이터 변경할 때는 트렌젝션 필요
@Transactional(readOnly = true) // readOnly = true --> JPA 조회하는 곳에서는 성능 최적화 가능
@RequiredArgsConstructor // final 가진 애만 생성자 만들어줌.
public class MemberService {
    // 생성자 인젝션(+final)
    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional // readOnly = false
    public Long join(Member member){

        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);

        return member.getId();
    }

    private void validateDuplicateMember(Member member){
        List<Member> findMembers = memberRepository.findByName(member.getName());
        /*
         추가 개발 : 동시에 가입하면 문제가 생길 수 있다. (멀티 스레드 등)
         getName -> unique 제약 조건 만들어보기
         */

        if (!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    // 회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }
    
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
