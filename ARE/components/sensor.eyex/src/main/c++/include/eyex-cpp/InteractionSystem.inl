/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionSystem.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONSYSTEM__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONSYSTEM__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline InteractionSystem::InteractionSystem(
	TX_SYSTEMCOMPONENTOVERRIDEFLAGS flags,
	TX_LOGGINGMODEL* pLoggingModel,
	TX_THREADINGMODEL* pThreadingModel,
	TX_SCHEDULINGMODEL* pSchedulingModel)
{
	TX_VALIDATE(txInitializeSystem(flags, pLoggingModel, pThreadingModel, pSchedulingModel));
}

/*********************************************************************************************************************/

inline InteractionSystem::~InteractionSystem()
{
	TX_VALIDATE(txUninitializeSystem());
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionSystem> InteractionSystem::Initialize(
	TX_SYSTEMCOMPONENTOVERRIDEFLAGS flags,
	TX_LOGGINGMODEL* pLoggingModel,
	TX_THREADINGMODEL* pThreadingModel,
	TX_SCHEDULINGMODEL* pSchedulingModel)
{
	return std::make_shared<InteractionSystem>(flags, pLoggingModel, pThreadingModel, pSchedulingModel);
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONSYSTEM__INL__)

/*********************************************************************************************************************/
