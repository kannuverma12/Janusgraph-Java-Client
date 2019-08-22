package com.paytm.digital.education.application.bootstrap;

import com.paytm.digital.education.database.entity.Stream;
import com.paytm.digital.education.database.repository.StreamRepository;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.entity.State;
import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.database.repository.ExamRepository;
import com.paytm.digital.education.explore.database.repository.InstituteRepository;
import com.paytm.digital.education.explore.database.repository.StateRepository;
import com.paytm.digital.education.explore.database.repository.SubscriptionRepository;
import com.paytm.digital.education.explore.enums.StateType;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;
import com.paytm.digital.education.explore.enums.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@RequiredArgsConstructor
public class DevBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private static final String DELHI_STATE = "Delhi";
    private static final String ENGINEERING_STREAM = "ENGINEERING_AND_ARCHITECTURE";

    @Value("${recreate-data:false}")
    private boolean recreateData;
    private final StateRepository stateRepository;
    private final StreamRepository streamRepository;
    private final ExamRepository examRepository;
    private final InstituteRepository instituteRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (this.recreateData) {
            recreateData();
        }
    }

    private void recreateData() {
        stateRepository.deleteAll();
        streamRepository.deleteAll();
        instituteRepository.deleteAll();
        examRepository.deleteAll();

        State delhi = new State(DELHI_STATE, StateType.STATE);
        stateRepository.save(delhi);

        Stream eng = Stream.builder().name(ENGINEERING_STREAM).build();
        streamRepository.save(eng);

        Institute inst1 = new Institute("inst1", 1L);
        Institute inst2 = new Institute("inst2", 2L);
        Institute inst3 = new Institute("inst3", 3L);
        Institute inst4 = new Institute("inst4", 4L);

        instituteRepository.save(inst1);
        instituteRepository.save(inst2);
        instituteRepository.save(inst3);
        instituteRepository.save(inst4);

        Exam exm1 = new Exam("exam1", 1L);
        Exam exm2 = new Exam("exam2", 2L);
        Exam exm3 = new Exam("exam3", 3L);
        Exam exm4 = new Exam("exam4", 4L);

        examRepository.save(exm1);
        examRepository.save(exm2);
        examRepository.save(exm3);
        examRepository.save(exm4);

        Long userId = 456712L;

        subscriptionRepository.deleteAll();

        Subscription s1 = new Subscription(userId, SubscribableEntityType.INSTITUTE, inst1.getInstituteId(),
            SubscriptionStatus.SUBSCRIBED);

        Subscription s2 = new Subscription(userId, SubscribableEntityType.INSTITUTE, inst2.getInstituteId(),
            SubscriptionStatus.SUBSCRIBED);

        Subscription s3 = new Subscription(userId, SubscribableEntityType.INSTITUTE, inst3.getInstituteId(),
            SubscriptionStatus.SUBSCRIBED);

        Subscription e1 = new Subscription(userId, SubscribableEntityType.EXAM, exm1.getExamId(),
            SubscriptionStatus.SUBSCRIBED);

        Subscription e2 = new Subscription(userId, SubscribableEntityType.EXAM, exm2.getExamId(),
            SubscriptionStatus.SUBSCRIBED);

        subscriptionRepository.save(s1);
        subscriptionRepository.save(s2);
        subscriptionRepository.save(s3);
        subscriptionRepository.save(e1);
        subscriptionRepository.save(e2);
    }
}
