/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionAgentBase.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONAGENTBASE__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONAGENTBASE__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline InteractionAgentBase::InteractionAgentBase(bool trackObjects) :
_trackObjects(trackObjects)
{ 
    memset(&_defaultLoggingModel, 0, sizeof(_defaultLoggingModel));
	_defaultLoggingModel.Targets = TX_LOGTARGET_CONSOLE;

#if _DEBUG
	_defaultLoggingModel.Targets = (TX_LOGTARGET)(_defaultLoggingModel.Targets | TX_LOGTARGET_TRACE);
#endif
}

/*********************************************************************************************************************/

inline void InteractionAgentBase::Initialize()
{
    _isRunning = true;
	_spSystem = InitializeEyeX();
	_spContext = Context::Create(_trackObjects);

	_connectionStateChangedHandlerTicket = _spContext->RegisterConnectionStateChangedHandler([this] (TX_CONNECTIONSTATE state) {
		OnConnectionStateChanged(state);
	});

	auto eventHandlerTicket = _spContext->RegisterMessageHandler(TX_MESSAGETYPE_EVENT, nullptr, [this] (const std::unique_ptr<AsyncData>& upAsyncData) {
		auto spEvent = upAsyncData->GetDataAs<InteractionEvent>();
		OnEvent(spEvent);
	});

	auto notificationHandlerTicket = _spContext->RegisterMessageHandler(TX_MESSAGETYPE_NOTIFICATION, nullptr, [this] (const std::unique_ptr<AsyncData>& upAsyncData) {
		auto spNotification = upAsyncData->GetDataAs<Notification>();
		OnNotification(spNotification);
	});

	_messageHandlerTickets.push_back(eventHandlerTicket);
	_messageHandlerTickets.push_back(notificationHandlerTicket);

	OnRegisterQueryHandlers();
	OnInitialize();

	_spContext->EnableConnection();    
}

/*********************************************************************************************************************/

inline void InteractionAgentBase::Uninitialize()
{
    _isRunning = false;

	OnUninitialize();
	_spContext->DisableConnection();

	for(auto messageHandlerTicket : _messageHandlerTickets)
		_spContext->UnregisterMessageHandler(messageHandlerTicket);

	_spContext->Shutdown();
    _spContext->UnregisterConnectionStateChangedHandler(_connectionStateChangedHandlerTicket);

	_spContext.reset();
	_spSystem.reset();
}

/*********************************************************************************************************************/

inline std::shared_ptr<Context> InteractionAgentBase::GetContext() const
{
	return _spContext;
}

/*********************************************************************************************************************/

inline std::shared_ptr<Environment> InteractionAgentBase::InitializeEyeX()
{
	auto pLoggingModel = GetLoggingModel();
	auto pThreadingModel = GetThreadingModel();
	auto pSchedulingModel = GetSchedulingModel();
   
	auto overrideFlags = TX_EYEXCOMPONENTOVERRIDEFLAG_NONE;

	if(pLoggingModel)
		overrideFlags = (TX_EYEXCOMPONENTOVERRIDEFLAGS)(overrideFlags | TX_EYEXCOMPONENTOVERRIDEFLAG_LOGGINGMODEL);
		
	if(pThreadingModel)
		overrideFlags = (TX_EYEXCOMPONENTOVERRIDEFLAGS)(overrideFlags | TX_EYEXCOMPONENTOVERRIDEFLAG_INTERNAL_THREADINGMODEL);

	if(pSchedulingModel)
		overrideFlags = (TX_EYEXCOMPONENTOVERRIDEFLAGS)(overrideFlags | TX_EYEXCOMPONENTOVERRIDEFLAG_INTERNAL_SCHEDULINGMODEL);

	return Environment::Initialize(overrideFlags, pLoggingModel, pThreadingModel, pSchedulingModel, nullptr);
}

/*********************************************************************************************************************/

inline void InteractionAgentBase::OnRegisterQueryHandlers()
{
	auto currentProcessId = GetCurrentProcessId();
	auto currentProcessIdStr = std::to_string(currentProcessId);
	RegisterQueryHandler(currentProcessIdStr);
}

/*********************************************************************************************************************/

inline void InteractionAgentBase::RegisterQueryHandler(const std::string& processId)
{
	auto spOptions = _spContext->CreateBag();
	auto spProcessIdProeprty = spOptions->CreateProperty(TX_LITERAL_TARGETPROCESSID);
	spProcessIdProeprty->SetValue(processId);

	auto fnQueryHandler = [this](const std::unique_ptr<AsyncData>& upAsyncData) 
	{
		auto spQuery = upAsyncData->GetDataAs<Query>();       
		OnQuery(spQuery);       
	};

	auto ticket = _spContext->RegisterMessageHandler(TX_MESSAGETYPE_QUERY, spOptions, fnQueryHandler);
	_messageHandlerTickets.push_back(ticket);
}

/*********************************************************************************************************************/

inline TX_LOGGINGMODEL* InteractionAgentBase::GetLoggingModel()
{
	return &_defaultLoggingModel;	
}

/*********************************************************************************************************************/

inline bool InteractionAgentBase::IsRunning() const
{
    return _isRunning;
}

/*********************************************************************************************************************/

inline AutoRespondingInteractionAgentBase::AutoRespondingInteractionAgentBase(bool trackObjects)
	: InteractionAgentBase(trackObjects)
{ }

/*********************************************************************************************************************/

inline void AutoRespondingInteractionAgentBase::OnQuery(const std::shared_ptr<Query>& spQuery)
{	    
    auto spSnapshot = Snapshot::CreateSnapshotForQuery(spQuery);

    try
    {
	    if(!PrepareSnapshot(spSnapshot))
		    return;
    }
    catch(...)
    {         
        GetContext()->WriteLogMessage(TX_LOGLEVEL_ERROR, "AutoRespondingInteractionAgentBase", "Custom snapshot preparation throw an exception");
		return;
    }

	spSnapshot->CommitAsync([this](const std::unique_ptr<AsyncData>& upAsyncData)
	{
		OnSnapshotResult(upAsyncData);	
	});
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONAGENTBASE__INL__)

/*********************************************************************************************************************/
