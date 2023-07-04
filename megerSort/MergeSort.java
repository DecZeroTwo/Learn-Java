

public class MergeSort {
    public static void main(String[] args) {
        int[] a= {3, 431, 4, 254, 35, 24, 134, 32, 14, 325, 341};
        int[] mergeArr = new int[a.length];
        mergeSort(a, 0, a.length - 1);
        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i]);
        }
    }

    private static void mergeSort(int[] arr, int left, int right) {
        //arr是要被排序的数组 mergeArr是合并的数组
        if (left >= right) {
            return;
        }
        int middle = (left + right) / 2;//分割
        mergeSort(arr, left, middle);//拿到左边的数组
        mergeSort(arr, middle + 1, right);//拿到右边的数组
        int[] leftArr = new int[middle - left + 1];
        for (int i = 0; i < leftArr.length; i++) {
            leftArr[i] = arr[left + i];
        }
        int[] rightArr = new int[right - middle];
        for (int i = 0; i < rightArr.length; i++) {
            rightArr[i] = arr[middle + i +1];
        }
        int[] mergeArr = new int[leftArr.length+rightArr.length];
        int i = 0;//mergeArr的下标
        int j = 0;//leftArr的下标
        int k = 0;//rightArr的下标
        while (j < leftArr.length && k < rightArr.length) {
            //谁小谁就放进mergeArr
            if (leftArr[j] < rightArr[k]) {
                mergeArr[i++] = leftArr[j++];
            } else {
                mergeArr[i++] = rightArr[k++];
            }
        }
        //如果leftArr还有剩余的数据，全部写进mergeArr的空余位置
        while (j < leftArr.length) {
            mergeArr[i++] = leftArr[j++];
        }
        //如果rightArr还有剩余的数据，全部写进mergeArr的空余位置
        while (k < rightArr.length) {
            mergeArr[i++] = rightArr[k++];
        }
        //把排好序的数组写回原数组
        for (int o = 0; o < mergeArr.length; o++) {
            arr[left + o] = mergeArr[o];
        }
    }
}