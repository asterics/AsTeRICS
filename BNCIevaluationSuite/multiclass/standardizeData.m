function normalizeData=standardizeData(data)
normFactor=var(reshape(data,[size(data,1),size(data,2)*size(data,3)]),0,2);
for i=1:size(data,1)
    normalizeData(i,:,:)=data(i,:,:)/normFactor(i);
end
