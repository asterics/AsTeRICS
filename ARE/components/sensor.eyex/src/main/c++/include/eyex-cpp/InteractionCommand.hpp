/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionCommand.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONCOMMAND__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONCOMMAND__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

class InteractionCommand :
	public InteractionObject
{
public:
	InteractionCommand(const std::shared_ptr<const InteractionContext>& spContext, TX_HANDLE hCommand);

	TX_INTERACTIONCOMMANDTYPE GetType() const;
	void ExecuteAsync(AsyncDataHandler fnHandler);
	
public:
	std::shared_ptr<InteractionObject> GetData() const;
	void SetData(const std::shared_ptr<InteractionObject>& spData);
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONCOMMAND__HPP__)

/*********************************************************************************************************************/
