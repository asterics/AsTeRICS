/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionCommand.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONCOMMAND__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONCOMMAND__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline InteractionCommand::InteractionCommand(const std::shared_ptr<const InteractionContext>& spContext, TX_HANDLE hCommand)
: InteractionObject(spContext, hCommand)
{}

/*********************************************************************************************************************/

inline TX_INTERACTIONCOMMANDTYPE InteractionCommand::GetType() const
{
	TX_INTERACTIONCOMMANDTYPE commandType;
	TX_VALIDATE(txGetCommandType(_hObject, &commandType));
	return commandType;
}

/*********************************************************************************************************************/

inline void InteractionCommand::ExecuteAsync(AsyncDataHandler fnHandler)
{	
    auto spThis = shared_from_this();
	auto fnProxy = [&, spThis, fnHandler](TX_CONSTHANDLE hAsyncData) 
	{			
		GetContext()->InvokeAsyncDataHandler(hAsyncData, fnHandler);
	};

    TX_VALIDATE(Tx::ExecuteCommandAsync(_hObject, fnProxy));
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionObject> InteractionCommand::GetData() const
{
	auto spProperty = GetProperty(TX_LITERAL_DATA);

	std::shared_ptr<InteractionObject> spData;
	if(spProperty->TryGetValue(&spData))
		return spData;

	return nullptr;
}

/*********************************************************************************************************************/
	
inline void InteractionCommand::SetData(const std::shared_ptr<InteractionObject>& spData)
{
	auto spProperty = GetProperty(TX_LITERAL_DATA);
	spProperty->SetValue(spData);
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONCOMMAND__INL__)

/*********************************************************************************************************************/
