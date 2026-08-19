package com.fixinh.smt.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixinh.smt.modules.province.entity.Province;
import com.fixinh.smt.modules.province.repository.ProvinceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class ProvinceSeeder implements CommandLineRunner {

    @Autowired
    private ProvinceRepository provinceRepository;

    @Override
    public void run(String... args) throws Exception {
        if (provinceRepository.count() > 0) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        InputStream in = new ClassPathResource("seed/provinces.json").getInputStream();
        List<Province> provinces = mapper.readValue(in, mapper.getTypeFactory()
                .constructCollectionType(List.class, Province.class));

        provinceRepository.saveAll(provinces);
        System.out.println("Seeded " + provinces.size() + " provinces.");
    }
}
