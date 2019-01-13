package ch.fad.ucd.ucdrepoupload.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import ch.fad.ucd.ucdrepoupload.model.ComponentType;
import ch.fad.ucd.ucdrepoupload.model.UcdComponent;
import ch.fad.ucd.ucdrepoupload.model.Version;
import ch.fad.ucd.ucdrepoupload.services.UcdComponentService;

/**
 * Create some Data
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final UcdComponentService ucdComponentService;

    public DataLoader(UcdComponentService ucdComponentService) {
        this.ucdComponentService = ucdComponentService;
    }

    @Override
    public void run(String... args) throws Exception {

        int count = this.ucdComponentService.findAll().size();

        if (count == 0) {
            loadData();
        }
    }

    private void loadData() {
        UcdComponent cadi = new UcdComponent("cadi", "/repolox/application/cadi", new ComponentType("application"));
        Version cadiVersion1 = new Version("1.0.1");
        Version cadiVersion2 = new Version("1.0.2");
        Version cadiVersion3 = new Version("1.0.3");
        Version cadiVersion4 = new Version("1.0.4");
        cadi.addVersion(cadiVersion1);
        cadi.addVersion(cadiVersion2);
        cadi.addVersion(cadiVersion3);
        cadi.addVersion(cadiVersion4);

        ucdComponentService.save(cadi);

        UcdComponent sibad = new UcdComponent("sibad", "/repolox/application/sibad", new ComponentType("application"));
        Version sibadVersion1 = new Version("1.0.1");
        Version sibadVersion2 = new Version("1.0.2");
        Version sibadVersion3 = new Version("1.0.3");
        Version sibadVersion4 = new Version("1.0.4");
        sibad.addVersion(sibadVersion1);
        sibad.addVersion(sibadVersion2);
        sibad.addVersion(sibadVersion3);
        sibad.addVersion(sibadVersion4);

        ucdComponentService.save(sibad);

        System.out.println("Loaded ucdcomponents....");
    }
}