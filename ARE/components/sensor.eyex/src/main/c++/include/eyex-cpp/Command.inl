/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Command.inl
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Command__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_Command__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

inline Command::Command(const std::shared_ptr<const Context>& spContext, TX_HANDLE hCommand)
: InteractionObject(spContext, hCommand)
{}

/*********************************************************************************************************************/

inline TX_COMMANDTYPE Command::GetType() const
{
	TX_COMMANDTYPE commandType;
	TX_VALIDATE(txGetCommandType(_hObject, &commandType));
	return commandType;
}

/*********************************************************************************************************************/

inline void Command::ExecuteAsync(AsyncDataHandler fnHandler)
{	
    auto spThis = shared_from_this();
	auto fnProxy = [&, spThis, fnHandler](TX_CONSTHANDLE hAsyncData) 
	{			
		GetContext()->InvokeAsyncDataHandler(hAsyncData, fnHandler);
	};

    TX_VALIDATE(Tx::ExecuteCommandAsync(_hObject, fnProxy));
}

/*********************************************************************************************************************/

inline std::shared_ptr<InteractionObject> Command::GetData() const
{
	auto spProperty = GetProperty(TX_LITERAL_DATA);

	std::shared_ptr<InteractionObject> spData;
	if(spProperty->TryGetValue(&spData))
		return spData;

	return nullptr;
}

/*********************************************************************************************************************/
	
inline void Command::SetData(const std::shared_ptr<InteractionObject>& spData)
{
	auto spProperty = GetProperty(TX_LITERAL_DATA);
	spProperty->SetValue(spData);
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Command__INL__)

/*********************************************************************************************************************/
