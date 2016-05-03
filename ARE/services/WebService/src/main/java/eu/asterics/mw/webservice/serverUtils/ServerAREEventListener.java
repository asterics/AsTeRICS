package eu.asterics.mw.webservice.serverUtils;

import eu.asterics.mw.are.AREEvent;
import eu.asterics.mw.services.IAREEventListener;
import eu.asterics.mw.webservice.SseResource;

/**
 * 
 * @author Marios Komodromos
 *
 */
public class ServerAREEventListener implements IAREEventListener {
	
	@Override
	public void preDeployModel() {
		SseResource.broadcastEvent(ServerEventType.MODEL_CHANGED, AREEvent.PRE_DEPLOY_EVENT.toString());
	}

	@Override
	public void postDeployModel() {
		SseResource.broadcastEvent(ServerEventType.MODEL_CHANGED, AREEvent.POST_DEPLOY_EVENT.toString());
	}

	@Override
	public void preStartModel() {
		SseResource.broadcastEvent(ServerEventType.MODEL_STATE_CHANGED, AREEvent.PRE_START_EVENT.toString());
	}
	
	@Override
	public void postStartModel() {
		SseResource.broadcastEvent(ServerEventType.MODEL_STATE_CHANGED, AREEvent.POST_START_EVENT.toString());
	}

	@Override
	public void preStopModel() {
		SseResource.broadcastEvent(ServerEventType.MODEL_STATE_CHANGED, AREEvent.PRE_STOP_EVENT.toString());
		
	}
	
	@Override
	public void postStopModel() {
		SseResource.broadcastEvent(ServerEventType.MODEL_STATE_CHANGED, AREEvent.POST_STOP_EVENT.toString());
	}

	@Override
	public void prePauseModel() {
		SseResource.broadcastEvent(ServerEventType.MODEL_STATE_CHANGED, AREEvent.PRE_PAUSE_EVENT.toString());
	}

	@Override
	public void postPauseModel() {
		SseResource.broadcastEvent(ServerEventType.MODEL_STATE_CHANGED, AREEvent.POST_PAUSE_EVENT.toString());
	}

	@Override
	public void preResumeModel() {
		SseResource.broadcastEvent(ServerEventType.MODEL_STATE_CHANGED, AREEvent.PRE_RESUME_EVENT.toString());
	}

	@Override
	public void postResumeModel() {
		SseResource.broadcastEvent(ServerEventType.MODEL_STATE_CHANGED, AREEvent.POST_RESUME_EVENT.toString());
	}
	
	@Override
	public void preBundlesInstalled() {
		//NOT SUPPORTED
	}
	
	@Override
	public void postBundlesInstalled() {
		//NOT SUPPORTED
	}

	@Override
	public void onAreError(String msg) {
		//NOT SUPPORTED
	}

}
