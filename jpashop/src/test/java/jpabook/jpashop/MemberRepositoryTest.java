package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;

    @Test
    @Transactional
    public void testMember() throws Exception {
        // given
        Member member = new Member();
        member.setUsername("memberA");
        
        // when
        Long savedId = memberRepository.save(member);

        // then
        Member findMember = memberRepository.find(savedId);

        Assertions.assertThat(member.getUsername()).isEqualTo(findMember.getUsername());

        Assertions.assertThat(findMember).isEqualTo(member); // findMember == member
    }
}
