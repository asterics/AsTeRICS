/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Environment.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_ENVIRONMENT__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_ENVIRONMENT__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline Environment::Environment(
	TX_EYEXCOMPONENTOVERRIDEFLAGS flags,
	TX_LOGGINGMODEL* pLoggingModel,
	TX_THREADINGMODEL* pThreadingModel,
	TX_SCHEDULINGMODEL* pSchedulingModel,
    void* pMemoryModel)
{
    TX_VALIDATE(txInitializeEyeX(flags, pLoggingModel, pThreadingModel, pSchedulingModel, pMemoryModel));
}

/*********************************************************************************************************************/

inline Environment::~Environment()
{
	TX_VALIDATE(txUninitializeEyeX());
}

/*********************************************************************************************************************/

inline std::shared_ptr<Environment> Environment::Initialize(
	TX_EYEXCOMPONENTOVERRIDEFLAGS flags,
	TX_LOGGINGMODEL* pLoggingModel,
	TX_THREADINGMODEL* pThreadingModel,
	TX_SCHEDULINGMODEL* pSchedulingModel,
    void* pMemoryModel)
{
	return std::make_shared<Environment>(flags, pLoggingModel, pThreadingModel, pSchedulingModel, pMemoryModel);
}

/*********************************************************************************************************************/

inline TX_EYEXAVAILABILITY Environment::GetEyeXAvailability()
{
	TX_EYEXAVAILABILITY availability;
	TX_VALIDATE(txGetEyeXAvailability(&availability));
	return availability;
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_ENVIRONMENT__INL__)

/*********************************************************************************************************************/
