package com.paytm.digital.education.application.bootstrap;

import static com.paytm.digital.education.explore.enums.StateType.UT;
import static java.lang.System.out;

import com.paytm.digital.education.explore.database.entity.State;
import com.paytm.digital.education.explore.database.entity.Stream;
import com.paytm.digital.education.explore.database.repository.StateRepository;
import com.paytm.digital.education.explore.database.repository.StreamRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@AllArgsConstructor
public class DevBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private static final String DELHI_STATE = "Delhi";
    private static final String ENGINEERING_STREAM = "ENGINEERING_AND_ARCHITECTURE";

    private StateRepository stateRepository;
    private StreamRepository streamRepository;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {
        if (stateRepository.findStateByName(DELHI_STATE) == null) {
            State delhi = new State(DELHI_STATE, UT);
            stateRepository.save(delhi);
            out.println(delhi.getId());
        }

        if (streamRepository.findStreamByName(ENGINEERING_STREAM) == null) {
            Stream eng = new Stream(ENGINEERING_STREAM);
            streamRepository.save(eng);
        }
    }
}
