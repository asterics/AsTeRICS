/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Command.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Command__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_Command__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

class Command :
	public InteractionObject
{
public:
	Command(const std::shared_ptr<const Context>& spContext, TX_HANDLE hCommand);

	TX_COMMANDTYPE GetType() const;
	void ExecuteAsync(AsyncDataHandler fnHandler);
	
public:
	std::shared_ptr<InteractionObject> GetData() const;
	void SetData(const std::shared_ptr<InteractionObject>& spData);
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Command__HPP__)

/*********************************************************************************************************************/
