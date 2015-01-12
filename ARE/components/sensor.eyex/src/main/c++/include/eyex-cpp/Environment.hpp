/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Environment.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_ENVIRONMENT__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_ENVIRONMENT__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

class Environment 
{	
public:
	Environment(
		TX_EYEXCOMPONENTOVERRIDEFLAGS flags,
		TX_LOGGINGMODEL* pLoggingModel,
		TX_THREADINGMODEL* pThreadingModel,
		TX_SCHEDULINGMODEL* pSchedulingModel,
        void* pMemoryModel);

	virtual ~Environment();

	static std::shared_ptr<Environment> Initialize(
		TX_EYEXCOMPONENTOVERRIDEFLAGS flags,
		TX_LOGGINGMODEL* pLoggingModel,
		TX_THREADINGMODEL* pThreadingModel,
		TX_SCHEDULINGMODEL* pSchedulingModel,
        void* pMemoryModel);

	TX_EYEXAVAILABILITY GetEyeXAvailability();
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_ENVIRONMENT__HPP__)

/*********************************************************************************************************************/
