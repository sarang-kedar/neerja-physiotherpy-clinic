package com.neerjaphysio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neerjaphysio.model.MedicalCertificate;

@Repository
public interface MedicalCertificateRepository extends JpaRepository<MedicalCertificate, Long> {

}
