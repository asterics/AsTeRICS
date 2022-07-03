function decimatedData=starDecimateMultiClass(data,ratio)
for i=1:size(data,1)
    for j=1:size(data,3)
        decimatedData(i,:,j)=decimate(data(i,:,j),ratio);
    end
end
