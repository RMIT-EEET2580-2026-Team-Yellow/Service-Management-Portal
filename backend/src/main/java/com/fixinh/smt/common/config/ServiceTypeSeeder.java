package com.fixinh.smt.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixinh.smt.modules.servicetype.entity.ServiceType;
import com.fixinh.smt.modules.servicetype.repository.ServiceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@Order(2)
public class ServiceTypeSeeder implements CommandLineRunner {

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Override
    public void run(String... args) throws Exception {
        if (serviceTypeRepository.count() > 0) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        InputStream in = new ClassPathResource("seed/service_types.json").getInputStream();
        List<ServiceType> serviceTypes = mapper.readValue(in, mapper.getTypeFactory()
                .constructCollectionType(List.class, ServiceType.class));

        serviceTypeRepository.saveAll(serviceTypes);
        System.out.println("Seeded " + serviceTypes.size() + " service types.");
    }
}
