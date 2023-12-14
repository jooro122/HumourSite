package com.project.humour2.repository;

import com.project.humour2.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {

    Member findByEmail(String email);

    Member findByUserName(String userName);
}
