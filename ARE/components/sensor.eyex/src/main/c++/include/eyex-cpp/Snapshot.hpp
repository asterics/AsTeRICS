/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Snapshot.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Snapshot__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_Snapshot__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

class Snapshot :
	public InteractionObject
{
public:
	Snapshot(const std::shared_ptr<const Context>& spContext, TX_HANDLE hSnapshot);
	static std::shared_ptr<Snapshot> CreateSnapshotForQuery(const std::shared_ptr<Query>& spQuery);  
		
    std::shared_ptr<Bounds> GetBounds() const;
    std::shared_ptr<Bounds> CreateBounds(TX_BOUNDSTYPE boundsType);
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


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Snapshot__HPP__)

/*********************************************************************************************************************/
