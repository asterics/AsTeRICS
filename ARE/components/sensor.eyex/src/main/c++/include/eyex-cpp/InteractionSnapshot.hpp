/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * InteractionSnapshot.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONSNAPSHOT__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONSNAPSHOT__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

class InteractionSnapshot :
	public InteractionObject
{
public:
	InteractionSnapshot(const std::shared_ptr<const InteractionContext>& spContext, TX_HANDLE hSnapshot);
	static std::shared_ptr<InteractionSnapshot> CreateSnapshotForQuery(const std::shared_ptr<InteractionQuery>& spQuery);  
		
    std::shared_ptr<InteractionBounds> GetBounds() const;
    std::shared_ptr<InteractionBounds> CreateBounds(TX_INTERACTIONBOUNDSTYPE boundsType);
    void SetBoundsFromQuery();
    void DeleteBounds();
	
	std::vector<std::shared_ptr<Interactor>> GetInteractors() const;

    std::shared_ptr<Interactor> CreateInteractor(
		const std::string& interactorId,
		const std::string& parentId,
		const std::string& windowId);

	void RemoveInteractor(const std::string& interactorId);

	std::vector<std::string> GetWindowIds();
	void AddWindowId(const std::string& windowId);

    void CommitAsync(AsyncDataHandler fnHandler) const;
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTIONSNAPSHOT__HPP__)

/*********************************************************************************************************************/
