/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * StateBag.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_STATEBAG__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_STATEBAG__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN
	
/*********************************************************************************************************************/

class StateBag :
	public InteractionObject
{
public:
	StateBag(const std::shared_ptr<const Context>& spContext, TX_HANDLE hBag);
		
    std::string GetStatePath() const;
    
    template <typename TValue>
    bool TryGetStateValue(TValue* pValue, const std::string& valuePath) const;

    template <typename TValue>
    void SetStateValue(const std::string& valuePath, const TValue& value);
        
	void SetAsync(AsyncDataHandler fnCompletion = nullptr);

private:
    bool TryGetPropertyForStateValue(std::shared_ptr<Property>* pspProperty, const std::string& valuePath, bool createIfNotExists) const;
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_STATEBAG__HPP__)

/*********************************************************************************************************************/
