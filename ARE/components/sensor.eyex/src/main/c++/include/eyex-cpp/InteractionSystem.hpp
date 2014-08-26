/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionSystem.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONSYSTEM__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONSYSTEM__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

class InteractionSystem 
{	
public:
	InteractionSystem(
		TX_SYSTEMCOMPONENTOVERRIDEFLAGS flags,
		TX_LOGGINGMODEL* pLoggingModel,
		TX_THREADINGMODEL* pThreadingModel,
		TX_SCHEDULINGMODEL* pSchedulingModel);

	virtual ~InteractionSystem();

	static std::shared_ptr<InteractionSystem> Initialize(
		TX_SYSTEMCOMPONENTOVERRIDEFLAGS flags,
		TX_LOGGINGMODEL* pLoggingModel,
		TX_THREADINGMODEL* pThreadingModel,
		TX_SCHEDULINGMODEL* pSchedulingModel);
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONSYSTEM__HPP__)

/*********************************************************************************************************************/
