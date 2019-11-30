/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensilex.core.variable.api;

import java.net.*;
import org.opensilex.core.variable.dal.*;

/**
 *
 * @author vidalmor
 */
public class EntityCreationDTO extends EntityUpdateDTO {

    protected URI uri;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
    
    public EntityModel updateModel(EntityModel model) {
        model = super.updateModel(model);
        if (getUri() != null) {
            model.setUri(getUri());
        }
        
        return model;
    }
}
