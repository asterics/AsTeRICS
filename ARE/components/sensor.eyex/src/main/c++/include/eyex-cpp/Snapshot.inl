/*********************************************************************************************************************
* Copyright 2013-2014 Tobii Technology AB. All rights reserved.
* Snapshot.inl
*********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Snapshot__INL__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_Snapshot__INL__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

	/*********************************************************************************************************************/

	inline Snapshot::Snapshot(const std::shared_ptr<const Context>& spContext, TX_HANDLE hSnapshot)
	: InteractionObject(spContext, hSnapshot)
{}

/*********************************************************************************************************************/

inline std::shared_ptr<Snapshot> Snapshot::CreateSnapshotForQuery(const std::shared_ptr<Query>& spQuery)
{
	Tx::Utils::ScopedHandle hSnapshot;
	TX_VALIDATE(txCreateSnapshotForQuery(spQuery->GetHandle(), &hSnapshot));

	auto spContext = spQuery->GetContext();
	auto spSnapshot = spContext->CreateObject<Snapshot>(hSnapshot);
	return spSnapshot;
}

/*********************************************************************************************************************/

inline std::shared_ptr<Bounds> Snapshot::GetBounds() const
{
	Tx::Utils::ScopedHandle hBounds;
	if (!TX_VALIDATE(txGetSnapshotBounds(_hObject, &hBounds), TX_RESULT_NOTFOUND))
		return nullptr;

	auto spBounds = _spContext->CreateObject<Bounds>(hBounds);
	return spBounds;
}

/*********************************************************************************************************************/

inline std::shared_ptr<Bounds> Snapshot::CreateBounds(TX_BOUNDSTYPE boundsType)
{
	Tx::Utils::ScopedHandle hBounds;
	TX_VALIDATE(txCreateSnapshotBounds(_hObject, &hBounds, boundsType));    
	auto spBounds = _spContext->CreateObject<Bounds>(hBounds);
	return spBounds;
}

/*********************************************************************************************************************/

inline void Snapshot::DeleteBounds()
{
	TX_VALIDATE(txDeleteSnapshotBounds(_hObject));
}

/*********************************************************************************************************************/

inline std::vector<std::shared_ptr<Interactor>> Snapshot::GetInteractors() const
{
	std::vector<Tx::Utils::ScopedHandle> interactorHandles;
	TX_VALIDATE(Tx::Utils::GetBufferData(interactorHandles, txGetInteractors, _hObject));

	std::vector<std::shared_ptr<Interactor>> interactors;
	for(auto& hInteractor : interactorHandles)
	{
		auto spInteractor = _spContext->CreateObject<Interactor>(hInteractor);
		interactors.push_back(spInteractor);
	}

	return interactors;
}

/*********************************************************************************************************************/

inline std::shared_ptr<Interactor> Snapshot::CreateInteractor(
	const std::string& interactorId,
	const std::string& parentId,
	const std::string& windowId)
{
	Tx::Utils::ScopedHandle hInteractor;
	TX_VALIDATE(txCreateInteractor(_hObject, &hInteractor, interactorId.c_str(), parentId.c_str(), windowId.c_str()));
	auto spInteractor = _spContext->CreateObject<Interactor>(hInteractor);
	return spInteractor;
}

/*********************************************************************************************************************/

inline void Snapshot::RemoveInteractor(const std::string& interactorId)
{
	TX_VALIDATE(txRemoveInteractor(_hObject, interactorId.c_str()));
}

/*********************************************************************************************************************/

inline std::vector<std::string> Snapshot::GetWindowIds()
{
	int windowIdCount = 0;
	TX_VALIDATE(txGetSnapshotWindowIdCount(_hObject, &windowIdCount));

	std::vector<std::string> windowIds;
	for (int i = 0; i < windowIdCount; i++)
	{
		std::string windowId;
		TX_VALIDATE(Tx::Utils::GetString(&windowId, [i, this](TX_CHAR* pBuf, TX_SIZE* pSize) 
		{
			return txGetSnapshotWindowId(_hObject, i, pBuf, pSize);
		}));

		windowIds.push_back(windowId);
	}

	return windowIds;
}

/*********************************************************************************************************************/

inline void Snapshot::AddWindowId(const std::string& windowId)
{
	TX_VALIDATE(txAddSnapshotWindowId(_hObject, windowId.c_str()));
}

/*********************************************************************************************************************/

inline void Snapshot::CommitAsync(AsyncDataHandler fnHandler) const
{    
	auto spThis = shared_from_this();
	auto fnProxy = [&, spThis, fnHandler](TX_CONSTHANDLE hAsyncData) 
	{			
		GetContext()->InvokeAsyncDataHandler(hAsyncData, fnHandler);
	};

	TX_VALIDATE(Tx::CommitSnapshotAsync(_hObject, fnProxy));
}

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/

#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_Snapshot__INL__)

/*********************************************************************************************************************/
