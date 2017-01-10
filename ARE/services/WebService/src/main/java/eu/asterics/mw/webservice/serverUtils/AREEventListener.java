/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 * 
 * 
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.     
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 *
 *
 *                    homepage: http://www.asterics.org 
 *
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.mw.webservice.serverUtils;

import eu.asterics.mw.are.AREEvent;
import eu.asterics.mw.services.IAREEventListener;
import eu.asterics.mw.webservice.SseResource;

/**
 * 
 * @author Marios Komodromos
 *
 */
public class AREEventListener implements IAREEventListener {

    @Override
    public void preDeployModel() {
        SseResource.broadcastDeploymentEvent(AREEvent.PRE_DEPLOY_EVENT.toString());
    }

    @Override
    public void postDeployModel() {
        SseResource.broadcastDeploymentEvent(AREEvent.POST_DEPLOY_EVENT.toString());
        SseResource.initializeDataChannelListeners();
    }

    @Override
    public void preStartModel() {
        SseResource.broadcastModelStateEvent(AREEvent.PRE_START_EVENT.toString());
    }

    @Override
    public void postStartModel() {
        SseResource.broadcastModelStateEvent(AREEvent.POST_START_EVENT.toString());
    }

    @Override
    public void preStopModel() {
        SseResource.broadcastModelStateEvent(AREEvent.PRE_STOP_EVENT.toString());

    }

    @Override
    public void postStopModel() {
        SseResource.broadcastModelStateEvent(AREEvent.POST_STOP_EVENT.toString());
    }

    @Override
    public void prePauseModel() {
        SseResource.broadcastModelStateEvent(AREEvent.PRE_PAUSE_EVENT.toString());
    }

    @Override
    public void postPauseModel() {
        SseResource.broadcastModelStateEvent(AREEvent.POST_PAUSE_EVENT.toString());
    }

    @Override
    public void preResumeModel() {
        SseResource.broadcastModelStateEvent(AREEvent.PRE_RESUME_EVENT.toString());
    }

    @Override
    public void postResumeModel() {
        SseResource.broadcastModelStateEvent(AREEvent.POST_RESUME_EVENT.toString());
    }

    @Override
    public void preBundlesInstalled() {
        // NOT SUPPORTED
    }

    @Override
    public void postBundlesInstalled() {
        // NOT SUPPORTED
    }

    @Override
    public void onAreError(String msg) {
        // NOT SUPPORTED
    }

}
