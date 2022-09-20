/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * APIException.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_APIEXCEPTION__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_APIEXCEPTION__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

class APIException 
{
public:
	APIException(TX_RESULT result, const std::string& message = "")
		: _result(result), _message(message)
	{ }
	
	TX_RESULT  GetResult() const
	{
		return _result;
	}

	std::string GetMessage() const
	{
		return _message;
	}

private:
	TX_RESULT _result;
	std::string _message;
};

/*********************************************************************************************************************/

inline bool TX_VALIDATE(TX_RESULT result)
{
	if(result == TX_RESULT_OK)
		return true;

	throw EyeX::APIException(result, "Error"); 
}

/*********************************************************************************************************************/

inline bool TX_VALIDATE(TX_RESULT result, TX_RESULT falseResult)
{
	if(result == falseResult)
		return false;

	return TX_VALIDATE(result);
}

/*********************************************************************************************************************/

inline bool TX_VALIDATE(TX_RESULT result, TX_RESULT falseResult1, TX_RESULT falseResult2)
{
	if(result == falseResult2)
		return false;

	return TX_VALIDATE(result, falseResult1);
}

template <typename THandle1, typename THandle2>
inline std::string GetString(TX_RESULT (*pFn)(THandle1, TX_STRING, TX_SIZE*), THandle2 handle, TX_SIZE estimatedLength = 0)
{
    std::string str;
    TX_VALIDATE(GetString(&str, pFn, handle, estimatedLength));
    return str;
}

/*********************************************************************************************************************/

TX_NAMESPACE_END
	
/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_APIEXCEPTION__HPP__)

/*********************************************************************************************************************/
