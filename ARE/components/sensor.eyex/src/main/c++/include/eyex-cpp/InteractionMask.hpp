/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionMask.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONMASK__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONMASK__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN
	
/*********************************************************************************************************************/

class InteractionMask :
	public InteractionObject
{
public:
	InteractionMask(const std::shared_ptr<const InteractionContext>& spContext, TX_HANDLE hInteractionMask);
	int GetColumnCount() const;
    int GetRowCount() const;
    void GetData(std::vector<TX_BYTE>& data) const;
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONMASK__HPP__)

/*********************************************************************************************************************/
