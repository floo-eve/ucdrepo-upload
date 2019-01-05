package ch.fad.ucd.ucdrepoupload.services;

import java.util.List;

import ch.fad.ucd.ucdrepoupload.model.UcdComponent;

/**
 * UcdComponentService
 */
public interface UcdComponentService extends CrudService<UcdComponent, String> {

    public UcdComponent findByName(String name);

}