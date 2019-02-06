package com.paytm.digital.education.explore.bootstrap;

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

    private StateRepository stateRepository;
    private StreamRepository streamRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (stateRepository.findStateByName("Delhi") == null) {
            State delhi = new State("Delhi");
            stateRepository.save(delhi);
            out.println(delhi.getId());
        }

        if (streamRepository.findStreamByName("engineering") == null) {
            Stream eng = new Stream("engineering");
            streamRepository.save(eng);
        }
    }
}
