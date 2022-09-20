/*********************************************************************************************************************
* Copyright 2013-2014 Tobii Technology AB. All rights reserved.
* Context.inl
*********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Context__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_Context__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline std::shared_ptr<Context> Context::Create(bool trackObjects)
{
	return std::make_shared<Context>(trackObjects);
}

/*********************************************************************************************************************/

inline Context::Context(bool trackObjects)
	: _hContext(TX_EMPTY_HANDLE)
{
	TX_VALIDATE(txCreateContext(&_hContext, trackObjects ? TX_TRUE : TX_FALSE));
}

/*********************************************************************************************************************/

inline void Context::Shutdown()
{
	txShutdownContext(_hContext, 1000, TX_TRUE);
}

/*********************************************************************************************************************/

inline Context::~Context()
{
	txReleaseContext(&_hContext);
}

/*********************************************************************************************************************/

inline TX_CONTEXTHANDLE Context::GetHandle() const
{
	return _hContext;
}

/*********************************************************************************************************************/

inline void Context::SetName(const std::string& name)
{
	TX_VALIDATE(txSetContextName(_hContext, name.c_str()));
}

/*********************************************************************************************************************/

inline std::string Context::GetName() const
{
	std::string name;
	TX_VALIDATE(Tx::Utils::GetString(&name, txGetContextName, _hContext));
	return name;
}

/*********************************************************************************************************************/

inline TX_TICKET Context::RegisterConnectionStateChangedHandler(ConnectionStateChangedHandler fnConnectionStateChangedHandler)
{	
	TX_TICKET ticket;
	TX_VALIDATE(Tx::RegisterConnectionStateChangedHandler(_hContext, &ticket, fnConnectionStateChangedHandler));
	return ticket;
}

/*********************************************************************************************************************/

inline void Context::UnregisterConnectionStateChangedHandler(TX_TICKET ticket)
{
	TX_VALIDATE(txUnregisterConnectionStateChangedHandler(_hContext, ticket));
}

/*********************************************************************************************************************/

inline void Context::EnableConnection()
{
	TX_VALIDATE(txEnableConnection(_hContext));
}

/*********************************************************************************************************************/

inline void Context::DisableConnection()
{
	TX_VALIDATE(txDisableConnection(_hContext));
}

/*********************************************************************************************************************/

inline TX_TICKET Context::RegisterMessageHandler(TX_MESSAGETYPE messageType, std::shared_ptr<const InteractionObject> spOptions, AsyncDataHandler fnMessageHandler)
{    
	auto fnProxy = [&, fnMessageHandler](TX_CONSTHANDLE hAsyncData) 
	{	        
		InvokeAsyncDataHandler(hAsyncData, fnMessageHandler);
	};

	auto hOptions = spOptions ? spOptions->GetHandle() : nullptr;

	TX_TICKET ticket = 0;
	Tx::RegisterMessageHandler(_hContext, &ticket, messageType, hOptions, fnProxy);
	return ticket;
}

/*********************************************************************************************************************/

inline void Context::UnregisterMessageHandler(TX_TICKET ticket)
{	
	TX_VALIDATE(txUnregisterMessageHandler(_hContext, ticket));
}

/*********************************************************************************************************************/

inline void Context::RegisterStateObserver(const std::string& statePath)
{
	TX_VALIDATE(txRegisterStateObserver(_hContext, statePath.c_str()));
}

/*********************************************************************************************************************/

inline void Context::UnregisterStateObserver(const std::string& statePath)
{	
	TX_VALIDATE(txUnregisterStateObserver(_hContext, statePath.c_str()));
}

/*********************************************************************************************************************/

inline void Context::GetStateAsync(const std::string& statePath, AsyncDataHandler fnCompletion) const
{
	auto callback = [&, fnCompletion](TX_CONSTHANDLE hAsyncData)
	{   		
		InvokeAsyncDataHandler(hAsyncData, fnCompletion);
	};

	TX_VALIDATE(Tx::GetStateAsync(_hContext, statePath.c_str(), callback));
}

/*********************************************************************************************************************/

inline std::shared_ptr<StateBag> Context::GetState(const std::string& statePath) const
{
	Tx::Utils::ScopedHandle hStateBag;
	TX_VALIDATE(txGetState(_hContext, statePath.c_str(), &hStateBag));

	std::shared_ptr<StateBag> spStateBag;
	if(hStateBag)
		spStateBag = CreateObject<StateBag>(hStateBag);

	return spStateBag;
}

/*********************************************************************************************************************/

inline TX_TICKET Context::RegisterStateChangedHandler(const std::string& statePath, AsyncDataHandler fnHandler)
{
	auto callback = [&, fnHandler](TX_CONSTHANDLE hAsyncData)
	{           
		InvokeAsyncDataHandler(hAsyncData, fnHandler);
	};
	
	TX_TICKET ticket;
	TX_VALIDATE(Tx::RegisterStateChangedHandler(_hContext, &ticket, statePath.c_str(), callback));
	
	return ticket;        
}

/*********************************************************************************************************************/

inline void Context::UnregisterStateChangedHandler(TX_TICKET ticket)
{
	TX_VALIDATE(txUnregisterStateChangedHandler(_hContext, ticket));
}

/*********************************************************************************************************************/

template <typename TValue>
inline void Context::SetStateAsync(const std::string& statePath, const TValue& value, AsyncDataHandler fnCompletion)
{
	auto stateBag = CreateStateBag(statePath);
	stateBag->SetStateValue(statePath, value);
	stateBag->SetAsync(fnCompletion);
}

/*********************************************************************************************************************/

inline std::vector<std::shared_ptr<InteractionObject>> Context::GetTrackedObjects() const
{
	std::vector<Tx::Utils::ScopedHandle> objectHandles;
	TX_VALIDATE(Tx::Utils::GetBufferData(objectHandles, txGetTrackedObjects, _hContext));

	std::vector<std::shared_ptr<InteractionObject>> objects;

	for(auto& hObject : objectHandles)
	{
		auto spObject = CreateObject<InteractionObject>(hObject);
		objects.push_back(spObject);
	}

	return objects;
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionObject> Context::CreateObject(TX_HANDLE hObject) const
{
	TX_INTERACTIONOBJECTTYPE objectType;
	TX_VALIDATE(txGetObjectType(hObject, &objectType));

	switch(objectType)
	{
	case TX_INTERACTIONOBJECTTYPE_BEHAVIOR:
		return CreateObject<Behavior>(hObject);

	case TX_INTERACTIONOBJECTTYPE_BOUNDS:
		return CreateObject<Bounds>(hObject);

	case TX_INTERACTIONOBJECTTYPE_COMMAND:
		return CreateObject<Command>(hObject);
		
	case TX_INTERACTIONOBJECTTYPE_QUERY:
		return CreateObject<Query>(hObject);

	case TX_INTERACTIONOBJECTTYPE_EVENT:
		return CreateObject<InteractionEvent>(hObject);

	case TX_INTERACTIONOBJECTTYPE_INTERACTOR:
		return CreateObject<Interactor>(hObject);

	case TX_INTERACTIONOBJECTTYPE_SNAPSHOT:
		return CreateObject<Snapshot>(hObject);

	case TX_INTERACTIONOBJECTTYPE_PROPERTYBAG:
		return CreateObject<PropertyBag>(hObject);

	case TX_INTERACTIONOBJECTTYPE_STATEBAG:
		return CreateObject<StateBag>(hObject);

	case TX_INTERACTIONOBJECTTYPE_NOTIFICATION:
		return CreateObject<Notification>(hObject);
	}

	throw APIException(TX_RESULT_UNKNOWN, "Unknown interaction object type");
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionObject> Context::CreateObject(Tx::Utils::ScopedHandle& hObject) const
{
	auto spObject = CreateObject((TX_HANDLE)hObject);

	if(spObject)
		hObject.Detach();

	return spObject;
}

/*********************************************************************************************************************/

inline std::shared_ptr<Property> Context::CreateProperty(TX_PROPERTYHANDLE hProperty) const
{
	auto spProperty = std::make_shared<Property>(shared_from_this(), hProperty);
	return spProperty;
}

/*********************************************************************************************************************/

inline std::shared_ptr<PropertyBag> Context::CreateBag(TX_PROPERTYBAGTYPE bagType) const
{
	Tx::Utils::ScopedHandle hBag;
	TX_VALIDATE(txCreatePropertyBag(_hContext, &hBag, bagType));
	auto spBag = CreateObject<PropertyBag>(hBag);
	return spBag;
}

/*********************************************************************************************************************/

inline std::shared_ptr<StateBag> Context::CreateStateBag(const std::string& statePath) const
{
	Tx::Utils::ScopedHandle hStateBag;
	TX_VALIDATE(txCreateStateBag(_hContext, &hStateBag, statePath.c_str()));	
	auto spStateBag = CreateObject<StateBag>(hStateBag);
	return spStateBag;
}

/*********************************************************************************************************************/

inline std::shared_ptr<Snapshot> Context::CreateSnapshot() const
{
	Tx::Utils::ScopedHandle hSnapshot;
	TX_VALIDATE(txCreateSnapshot(_hContext, &hSnapshot));
	auto spSnapshot = CreateObject<Snapshot>(hSnapshot);
	return spSnapshot;
}

/*********************************************************************************************************************/

inline std::shared_ptr<Snapshot> Context::CreateGlobalInteractorSnapshot(TX_CONSTSTRING globalInteractorId, std::shared_ptr<Interactor>* pspInteractor) const
{
	Tx::Utils::ScopedHandle hSnapshot, hInteractor;
	TX_VALIDATE(txCreateGlobalInteractorSnapshot(_hContext, globalInteractorId, &hSnapshot, &hInteractor));
	*pspInteractor = CreateObject<Interactor>(hInteractor);
	auto spSnapshot = CreateObject<Snapshot>(hSnapshot);
	return spSnapshot;
}

/*********************************************************************************************************************/

inline std::shared_ptr<Command> Context::CreateCommand(TX_COMMANDTYPE commandType) const
{
	Tx::Utils::ScopedHandle hCommand;
	TX_VALIDATE(txCreateCommand(_hContext, &hCommand, commandType));
	auto spCommand = CreateObject<Command>(hCommand);
	return spCommand;
}

/*********************************************************************************************************************/

inline std::shared_ptr<Command> Context::CreateActionCommand(TX_ACTIONTYPE actionType) const
{
    Tx::Utils::ScopedHandle hCommand;
    TX_VALIDATE(txCreateActionCommand(_hContext, &hCommand, actionType));
    auto spCommand = CreateObject<Command>(hCommand);
    return spCommand;
}

/*********************************************************************************************************************/

inline void Context::DisableBuiltinKeys(const std::string& windowId, AsyncDataHandler fnCompletion) const
{
    auto callback = [&, fnCompletion](TX_CONSTHANDLE hAsyncData)
	{   		
		InvokeAsyncDataHandler(hAsyncData, fnCompletion);
	};

    TX_VALIDATE(Tx::DisableBuiltinKeys(_hContext, windowId.c_str(), callback));
}

/*********************************************************************************************************************/

inline void Context::EnableBuiltinKeys(const std::string& windowId, AsyncDataHandler fnCompletion) const
{
    auto callback = [&, fnCompletion](TX_CONSTHANDLE hAsyncData)
	{   		
		InvokeAsyncDataHandler(hAsyncData, fnCompletion);
	};

    TX_VALIDATE(Tx::EnableBuiltinKeys(_hContext, windowId.c_str(), callback));
}

/*********************************************************************************************************************/

inline void Context::LaunchConfigurationTool(TX_CONFIGURATIONTOOL configurationTool, AsyncDataHandler fnCompletion) const
{
	auto callback = [&, fnCompletion](TX_CONSTHANDLE hAsyncData)
	{
		InvokeAsyncDataHandler(hAsyncData, fnCompletion);
	};

	TX_VALIDATE(Tx::LaunchConfigurationTool(_hContext, configurationTool, callback));
}

/*********************************************************************************************************************/

template <typename TInteractionObject>
inline std::shared_ptr<TInteractionObject> Context::CreateObject(TX_HANDLE hObject) const
{
	return std::make_shared<TInteractionObject>(shared_from_this(), hObject);
}

/*********************************************************************************************************************/

template <typename TInteractionObject>
inline std::shared_ptr<TInteractionObject> Context::CreateObject(Tx::Utils::ScopedHandle& hObject) const
{
	auto spObject = CreateObject<TInteractionObject>((TX_HANDLE)hObject);

	if(spObject)
		hObject.Detach();

	return spObject;
}

/*********************************************************************************************************************/

inline void Context::WriteLogMessage(TX_LOGLEVEL level, const std::string& scope, const std::string& message)
{
	TX_VALIDATE(txWriteLogMessage(level, scope.c_str(), message.c_str()));
}

/*********************************************************************************************************************/

inline void Context::PerformScheduledJobs()
{
	TX_VALIDATE(txPerformScheduledJobs(_hContext));
}

/*********************************************************************************************************************/

inline void Context::InvokeAsyncDataHandler(TX_CONSTHANDLE hAsyncData, AsyncDataHandler fnHandler) const
{ 
	if(!fnHandler)
		return;

	try
	{		
		auto upAsyncData = std::unique_ptr<AsyncData>(new AsyncData(shared_from_this(), hAsyncData));
		fnHandler(upAsyncData);
	}
	catch(...)
	{ }
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Context__INL__)

/*********************************************************************************************************************/
