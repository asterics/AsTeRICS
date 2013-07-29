function [borderIndex,joinedData]=joinDataSets(data1,data2)
    joinedData.features=cat(3,data1.features,data2.features);
%     size(data1.GT)
%     size(data2.GT)
    if size(data1.GT,1)==1
        data1.GT=transpose(data1.GT);
    end
    if size(data2.GT,1)==1
        data2.GT=transpose(data2.GT);
    end
    joinedData.GT=cat(1,data1.GT,data2.GT);
    borderIndex=size(data1.GT,1)+1;
    