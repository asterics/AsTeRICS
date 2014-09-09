/*********************************************************************************************************************
* Copyright 2013-2014 Tobii Technology AB. All rights reserved.
* InteractionContext.inl
*********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONCONTEXT__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONCONTEXT__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionContext> InteractionContext::Create(bool trackObjects)
{
	return std::make_shared<InteractionContext>(trackObjects);
}

/*********************************************************************************************************************/

inline InteractionContext::InteractionContext(bool trackObjects)
{
	TX_VALIDATE(txCreateContext(&_hContext, trackObjects ? TX_TRUE : TX_FALSE));
}

/*********************************************************************************************************************/

inline void InteractionContext::Shutdown()
{
	txShutdownContext(_hContext, 1000, TX_TRUE);
}

/*********************************************************************************************************************/

inline InteractionContext::~InteractionContext()
{
	txReleaseContext(&_hContext);
}

/*********************************************************************************************************************/

inline TX_CONTEXTHANDLE InteractionContext::GetHandle() const
{
	return _hContext;
}

/*********************************************************************************************************************/

inline TX_TICKET InteractionContext::RegisterConnectionStateChangedHandler(ConnectionStateChangedHandler fnConnectionStateChangedHandler)
{	
	TX_TICKET ticket;
	TX_VALIDATE(Tx::RegisterConnectionStateChangedHandler(_hContext, &ticket, fnConnectionStateChangedHandler));
	return ticket;
}

/*********************************************************************************************************************/

inline void InteractionContext::UnregisterConnectionStateChangedHandler(TX_TICKET ticket)
{
	TX_VALIDATE(txUnregisterConnectionStateChangedHandler(_hContext, ticket));
}

/*********************************************************************************************************************/

inline void InteractionContext::EnableConnection()
{
	TX_VALIDATE(txEnableConnection(_hContext));
}

/*********************************************************************************************************************/

inline void InteractionContext::DisableConnection()
{
	TX_VALIDATE(txDisableConnection(_hContext));
}

/*********************************************************************************************************************/

inline TX_TICKET InteractionContext::RegisterMessageHandler(TX_MESSAGETYPE messageType, std::shared_ptr<const InteractionObject> spOptions, AsyncDataHandler fnMessageHandler)
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

inline void InteractionContext::UnregisterMessageHandler(TX_TICKET ticket)
{	
	TX_VALIDATE(txUnregisterMessageHandler(_hContext, ticket));
}

/*********************************************************************************************************************/

inline void InteractionContext::RegisterStateObserver(const std::string& statePath)
{
	TX_VALIDATE(txRegisterStateObserver(_hContext, statePath.c_str()));
}

/*********************************************************************************************************************/

inline void InteractionContext::UnregisterStateObserver(const std::string& statePath)
{	
	TX_VALIDATE(txUnregisterStateObserver(_hContext, statePath.c_str()));
}

/*********************************************************************************************************************/

inline void InteractionContext::GetStateAsync(const std::string& statePath, AsyncDataHandler fnCompletion) const
{
	auto callback = [&, fnCompletion](TX_CONSTHANDLE hAsyncData)
	{   		
		InvokeAsyncDataHandler(hAsyncData, fnCompletion);
	};

	TX_VALIDATE(Tx::GetStateAsync(_hContext, statePath.c_str(), callback));
}

/*********************************************************************************************************************/

inline std::shared_ptr<StateBag> InteractionContext::GetState(const std::string& statePath) const
{
	Tx::Utils::ScopedHandle hStateBag;
	TX_VALIDATE(txGetState(_hContext, statePath.c_str(), &hStateBag));

	std::shared_ptr<StateBag> spStateBag;
	if(hStateBag)
		spStateBag = CreateObject<StateBag>(hStateBag);

	return spStateBag;
}

/*********************************************************************************************************************/

inline TX_TICKET InteractionContext::RegisterStateChangedHandler(const std::string& statePath, AsyncDataHandler fnHandler)
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

inline void InteractionContext::UnregisterStateChangedHandler(TX_TICKET ticket)
{
	TX_VALIDATE(txUnregisterStateChangedHandler(_hContext, ticket));
}

/*********************************************************************************************************************/

template <typename TValue>
inline void InteractionContext::SetStateAsync(const std::string& statePath, const TValue& value, AsyncDataHandler fnCompletion)
{
	auto stateBag = CreateStateBag(statePath);
	stateBag->SetStateValue(statePath, value);
	stateBag->SetAsync(fnCompletion);
}

/*********************************************************************************************************************/

inline std::vector<std::shared_ptr<InteractionObject>> InteractionContext::GetTrackedObjects() const
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

inline std::shared_ptr<InteractionObject> InteractionContext::CreateObject(TX_HANDLE hObject) const
{
	TX_INTERACTIONOBJECTTYPE objectType;
	TX_VALIDATE(txGetObjectType(hObject, &objectType));

	switch(objectType)
	{
	case TX_INTERACTIONOBJECTTYPE_BEHAVIOR:
		return CreateObject<InteractionBehavior>(hObject);

	case TX_INTERACTIONOBJECTTYPE_BOUNDS:
		return CreateObject<InteractionBounds>(hObject);

	case TX_INTERACTIONOBJECTTYPE_COMMAND:
		return CreateObject<InteractionCommand>(hObject);
		
	case TX_INTERACTIONOBJECTTYPE_QUERY:
		return CreateObject<InteractionQuery>(hObject);

	case TX_INTERACTIONOBJECTTYPE_EVENT:
		return CreateObject<InteractionEvent>(hObject);

	case TX_INTERACTIONOBJECTTYPE_INTERACTOR:
		return CreateObject<Interactor>(hObject);

	case TX_INTERACTIONOBJECTTYPE_SNAPSHOT:
		return CreateObject<InteractionSnapshot>(hObject);

	case TX_INTERACTIONOBJECTTYPE_PROPERTYBAG:
		return CreateObject<PropertyBag>(hObject);

	case TX_INTERACTIONOBJECTTYPE_STATEBAG:
		return CreateObject<StateBag>(hObject);

	case TX_INTERACTIONOBJECTTYPE_NOTIFICATION:
		return CreateObject<InteractionNotification>(hObject);
	}

	throw APIException(TX_RESULT_UNKNOWN, "Unknown interaction object type");
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionObject> InteractionContext::CreateObject(Tx::Utils::ScopedHandle& hObject) const
{
	auto spObject = CreateObject((TX_HANDLE)hObject);

	if(spObject)
		hObject.Detach();

	return spObject;
}

/*********************************************************************************************************************/

inline std::shared_ptr<Property> InteractionContext::CreateProperty(TX_PROPERTYHANDLE hProperty) const
{
	auto spProperty = std::make_shared<Property>(shared_from_this(), hProperty);
	return spProperty;
}

/*********************************************************************************************************************/

inline std::shared_ptr<PropertyBag> InteractionContext::CreateBag(TX_PROPERTYBAGTYPE bagType) const
{
	Tx::Utils::ScopedHandle hBag;
	TX_VALIDATE(txCreatePropertyBag(_hContext, &hBag, bagType));
	auto spBag = CreateObject<PropertyBag>(hBag);
	return spBag;
}

/*********************************************************************************************************************/

inline std::shared_ptr<StateBag> InteractionContext::CreateStateBag(const std::string& statePath) const
{
	Tx::Utils::ScopedHandle hStateBag;
	TX_VALIDATE(txCreateStateBag(_hContext, &hStateBag, statePath.c_str()));	
	auto spStateBag = CreateObject<StateBag>(hStateBag);
	return spStateBag;
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionSnapshot> InteractionContext::CreateSnapshot() const
{
	Tx::Utils::ScopedHandle hSnapshot;
	TX_VALIDATE(txCreateSnapshot(_hContext, &hSnapshot));
	auto spSnapshot = CreateObject<InteractionSnapshot>(hSnapshot);
	return spSnapshot;
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionSnapshot> InteractionContext::CreateGlobalInteractorSnapshot(TX_CONSTSTRING globalInteractorId, std::shared_ptr<Interactor>* pspInteractor) const
{
	Tx::Utils::ScopedHandle hSnapshot, hInteractor;
	TX_VALIDATE(txCreateGlobalInteractorSnapshot(_hContext, globalInteractorId, &hSnapshot, &hInteractor));
	*pspInteractor = CreateObject<Interactor>(hInteractor);
	auto spSnapshot = CreateObject<InteractionSnapshot>(hSnapshot);
	return spSnapshot;
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionCommand> InteractionContext::CreateCommand(TX_INTERACTIONCOMMANDTYPE commandType) const
{
	Tx::Utils::ScopedHandle hCommand;
	TX_VALIDATE(txCreateCommand(_hContext, &hCommand, commandType));
	auto spCommand = CreateObject<InteractionCommand>(hCommand);
	return spCommand;
}

/*********************************************************************************************************************/

template <typename TInteractionObject>
inline std::shared_ptr<TInteractionObject> InteractionContext::CreateObject(TX_HANDLE hObject) const
{
	return std::make_shared<TInteractionObject>(shared_from_this(), hObject);
}

/*********************************************************************************************************************/

template <typename TInteractionObject>
inline std::shared_ptr<TInteractionObject> InteractionContext::CreateObject(Tx::Utils::ScopedHandle& hObject) const
{
	auto spObject = CreateObject<TInteractionObject>((TX_HANDLE)hObject);

	if(spObject)
		hObject.Detach();

	return spObject;
}

/*********************************************************************************************************************/

inline void InteractionContext::WriteLogMessage(TX_LOGLEVEL level, const std::string& scope, const std::string& message)
{
	TX_VALIDATE(txWriteLogMessage(level, scope.c_str(), message.c_str()));
}

/*********************************************************************************************************************/

inline void InteractionContext::PerformScheduledJobs()
{
	TX_VALIDATE(txPerformScheduledJobs(_hContext));
}

/*********************************************************************************************************************/

inline void InteractionContext::InvokeAsyncDataHandler(TX_CONSTHANDLE hAsyncData, AsyncDataHandler fnHandler) const
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

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONCONTEXT__INL__)

/*********************************************************************************************************************/
