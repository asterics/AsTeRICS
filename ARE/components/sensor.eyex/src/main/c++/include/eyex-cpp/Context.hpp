/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Context.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Context__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_Context__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

class Context : 
    public std::enable_shared_from_this<Context>
{
public:
    static std::shared_ptr<Context> Create(bool trackObjects);
    Context(bool trackObjects);
    virtual ~Context();

	TX_CONTEXTHANDLE GetHandle() const;
	void SetName(const std::string& name);
	std::string GetName() const;

    TX_TICKET RegisterConnectionStateChangedHandler(ConnectionStateChangedHandler fnConnectionStateChangedHandler);
    void UnregisterConnectionStateChangedHandler(TX_TICKET ticket);

    void EnableConnection();
    void DisableConnection();
    void Shutdown();

    TX_TICKET RegisterMessageHandler(TX_MESSAGETYPE messageType, std::shared_ptr<const InteractionObject> spOptions, AsyncDataHandler fnAsyncDataHandler);
    void UnregisterMessageHandler(TX_TICKET ticket);
		
	void RegisterStateObserver(const std::string& statePath);
	void UnregisterStateObserver(const std::string& statePath);
	void GetStateAsync(const std::string& statePath, AsyncDataHandler fnCompletion) const;
    std::shared_ptr<StateBag> GetState(const std::string& statePath) const;

	TX_TICKET RegisterStateChangedHandler(const std::string& statePath, AsyncDataHandler fnHandler);
	void UnregisterStateChangedHandler(TX_TICKET ticket);
    
    template <typename TValue>
    void SetStateAsync(const std::string& statePath, const TValue& value, AsyncDataHandler fnCompletion = nullptr);

    std::vector<std::shared_ptr<InteractionObject>> GetTrackedObjects() const;

    std::shared_ptr<InteractionObject> CreateObject(TX_HANDLE hObject) const;
	std::shared_ptr<InteractionObject> CreateObject(Tx::Utils::ScopedHandle& hObject) const;
    std::shared_ptr<Property> CreateProperty(TX_PROPERTYHANDLE hProperty) const;

    std::shared_ptr<PropertyBag> CreateBag(TX_PROPERTYBAGTYPE bagType = TX_PROPERTYBAGTYPE_OBJECT) const;	
	std::shared_ptr<StateBag> CreateStateBag(const std::string& statePath) const;
    std::shared_ptr<Snapshot> CreateSnapshot() const;    
    std::shared_ptr<Snapshot> CreateGlobalInteractorSnapshot(TX_CONSTSTRING globalInteractorId, std::shared_ptr<Interactor>* pspInteractor) const;
    std::shared_ptr<Command> CreateCommand(TX_COMMANDTYPE commandType) const;
	std::shared_ptr<Command> CreateActionCommand(TX_ACTIONTYPE actionType) const;

    void DisableBuiltinKeys(const std::string& windowId, AsyncDataHandler fnCompletion = nullptr) const;
    void EnableBuiltinKeys(const std::string& windowId, AsyncDataHandler fnCompletion = nullptr) const;

	void LaunchConfigurationTool(TX_CONFIGURATIONTOOL configurationTool, AsyncDataHandler fnCompletion = nullptr) const;

    template <typename TInteractionObject>
    std::shared_ptr<TInteractionObject> CreateObject(TX_HANDLE hObject) const;
	
    template <typename TInteractionObject>
	std::shared_ptr<TInteractionObject> CreateObject(Tx::Utils::ScopedHandle& hObject) const;

    void WriteLogMessage(TX_LOGLEVEL level, const std::string& scope, const std::string& message);
	void PerformScheduledJobs();

	void InvokeAsyncDataHandler(TX_CONSTHANDLE hAsyncData, AsyncDataHandler fnHandler) const;

private:
    TX_CONTEXTHANDLE _hContext;
};

/*********************************************************************************************************************/

TX_NAMESPACE_END
	
/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Context__HPP__)

/*********************************************************************************************************************/
