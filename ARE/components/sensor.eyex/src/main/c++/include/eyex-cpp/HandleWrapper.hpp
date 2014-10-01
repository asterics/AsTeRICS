/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * HandleWrapper.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_HANDLEWRAPPER__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_HANDLEWRAPPER__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

template <typename THandle>
class HandleWrapper :
	public std::enable_shared_from_this<HandleWrapper<THandle>>
{
public:
    HandleWrapper(const std::shared_ptr<const Context>& spContext, THandle hObject);
	virtual ~HandleWrapper();
	
    std::shared_ptr<const Context> GetContext() const;
	THandle GetHandle() const;

protected:
    template <typename THandle1, typename THandle2>
    inline static std::string GetString(TX_RESULT (*pFn)(THandle1, TX_STRING, TX_SIZE*), THandle2 handle, TX_SIZE estimatedLength = 0);

protected:
	std::shared_ptr<const Context> _spContext;
	THandle _hObject;
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_HANDLEWRAPPER__HPP__)

/*********************************************************************************************************************/
