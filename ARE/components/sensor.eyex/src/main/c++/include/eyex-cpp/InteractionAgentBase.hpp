/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionAgentBase.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONAGENTBASE__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONAGENTBASE__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

class InteractionAgentBase 
{	
public:
	void Initialize();
	void Uninitialize();

	std::shared_ptr<Context> GetContext() const;

protected:
	InteractionAgentBase(bool trackObjects = true);

	virtual void OnInitialize() {}
	virtual void OnUninitialize() {}
	virtual void OnRegisterQueryHandlers();
	
	virtual void OnConnectionStateChanged(TX_CONNECTIONSTATE state) {}
	virtual void OnNotification(const std::shared_ptr<Notification>& spNotification) {}
	virtual void OnQuery(const std::shared_ptr<Query>& spQuery) {}
	virtual void OnEvent(const std::shared_ptr<InteractionEvent>& spEvent) {}

	virtual std::shared_ptr<Environment> InitializeEyeX();	
	virtual TX_LOGGINGMODEL* GetLoggingModel();
	virtual TX_THREADINGMODEL* GetThreadingModel() { return nullptr; }
	virtual TX_SCHEDULINGMODEL* GetSchedulingModel() { return nullptr; }
    
	void RegisterQueryHandler(const std::string& processId);

    bool IsRunning() const;
		
private:
    bool _isRunning;

	std::shared_ptr<Context> _spContext;
	TX_TICKET _connectionStateChangedHandlerTicket;
	std::vector<TX_TICKET> _messageHandlerTickets;
	std::shared_ptr<Environment> _spSystem;
    bool _trackObjects;

	TX_LOGGINGMODEL _defaultLoggingModel;
};

/*********************************************************************************************************************/

class AutoRespondingInteractionAgentBase : 
	public InteractionAgentBase
{
public:
	AutoRespondingInteractionAgentBase(bool trackObjects = true);

protected:
	void OnQuery(const std::shared_ptr<Query>& spQuery) override;

	virtual bool PrepareSnapshot(const std::shared_ptr<Snapshot>& spSnapshot) { return false; }
	virtual void OnSnapshotResult(const std::unique_ptr<AsyncData>& upAsyncData) {}
};

/*********************************************************************************************************************/

TX_NAMESPACE_END
	
/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONAGENTBASE__HPP__)

/*********************************************************************************************************************/
