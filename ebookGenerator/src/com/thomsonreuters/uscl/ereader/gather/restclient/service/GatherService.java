package com.thomsonreuters.uscl.ereader.gather.restclient.service;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherNortRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;

public interface GatherService {
    /**
     * Rest client request to gather url.
     * @param gatherTocRequest
     * @return
     */
    GatherResponse getToc(GatherTocRequest gatherTocRequest);

    /**
     * Rest client request to gather url.
     * @param gatherTocRequest
     * @return
     */
    GatherResponse getNort(GatherNortRequest gatherNortRequest);

    /**
     * Rest client request to gather url.
     * @param gatherDocRequest
     * @return
     */
    GatherResponse getDoc(GatherDocRequest gatherDocRequest);

    GatherResponse getImg(GatherImgRequest imgRequest);
}
