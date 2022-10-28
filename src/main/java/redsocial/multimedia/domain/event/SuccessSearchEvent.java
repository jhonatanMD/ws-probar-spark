package redsocial.multimedia.domain.event;

import com.superapp.redsocial.core.domain.constants.HttpCodes;
import com.superapp.redsocial.core.domain.events.SuccessfulEvent;
import redsocial.multimedia.domain.entities.ResponseMultimedia;

public class SuccessSearchEvent extends SuccessfulEvent {

    private String wm;

    public SuccessSearchEvent(ResponseMultimedia responseMultimedia) {
        super(HttpCodes.SUCCESS.code(), "Operación realizada con exito.");
        this.wm  =responseMultimedia.getUrlWm();
    }
}
